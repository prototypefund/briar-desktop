package org.briarproject.briar.desktop.builddata

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.revwalk.RevCommit
import org.gradle.api.GradleScriptException
import org.gradle.api.tasks.TaskAction
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.StandardCopyOption

open class GenerateBuildDataSourceTask : AbstractBuildDataTask() {
    init {
        group = "build"
    }

    @TaskAction
    @Throws(IOException::class)
    protected fun generateSource() {
        val project = project
        val packageName = configuration?.packageName
        var className = configuration?.className
        if (className == null) {
            className = "BuildData"
        }

        /*
		 * Get version from Gradle project information
		 */
        val version = project.version.toString()

        /*
		 * Get Git hash, last commit time and current branch using JGit
		 */
        // Open git repository
        val dir = project.projectDir
        val git = Git.open(dir)
        val repository = git.repository

        // Get head ref and it's name => current hash
        val head = repository.resolve(Constants.HEAD)
        val gitHash = head.name

        // Get latest commit and its commit time
        val first: RevCommit = try {
            getLastCommit(git)
        } catch (e: GitAPIException) {
            throw GradleScriptException("Error while fetching commits", e)
        } catch (e: NoSuchElementException) {
            throw GradleScriptException("Error while fetching commits", e)
        }

        // Convert from seconds to milliseconds
        val commitTime = first.commitTime * 1000L

        // Get current branch, if any
        var gitBranch = "<unknown>"
        val prefix = "refs/heads/"
        val fullBranch = repository.fullBranch
        if (fullBranch.startsWith(prefix)) {
            gitBranch = fullBranch.substring(prefix.length)
        }

        /*
		 * Generate output file
		 */
        checkNotNull(packageName) { "Please specify 'packageName'." }
        val parts = packageName.split("\\.".toRegex()).toTypedArray()
        val pathBuildDir = project.buildDir.toPath()
        val source = Util.getSourceDir(pathBuildDir)
        var path = source
        for (i in parts.indices) {
            path = path.resolve(parts[i])
        }
        Files.createDirectories(path)
        val file = path.resolve("$className.java")
        val content = createSource(
            packageName, className, version,
            commitTime, gitHash, gitBranch
        )
        val `in`: InputStream = ByteArrayInputStream(
            content.toByteArray(StandardCharsets.UTF_8)
        )
        Files.copy(`in`, file, StandardCopyOption.REPLACE_EXISTING)
    }

    @Throws(GitAPIException::class)
    private fun getLastCommit(git: Git): RevCommit {
        val commits = git.log().call()
        val iterator: Iterator<RevCommit> = commits.iterator()
        if (!iterator.hasNext()) {
            throw NoSuchElementException()
        }
        return iterator.next()
    }

    private fun createSource(
        packageName: String,
        className: String,
        version: String,
        gitTime: Long,
        gitHash: String,
        gitBranch: String,
    ): String {
        return FileBuilder().also {
            with(it) {
                line("// this file is generated, do not edit")
                // // this file is generated, do not edit
                // package org.briarproject.briar.desktop;
                //
                // public class BuildData {
                line("// this file is generated, do not edit")
                line("package $packageName;")
                line()
                line("public class $className {")
                line()
                // public static String getVersion() {
                //     return "0.1";
                // }
                line("    public static String getVersion() {")
                line("        return \"$version\";")
                line("    }")
                line()
                // public static long getGitTime() {
                //     return 1641645802088L;
                // }
                line("    public static long getGitTime() {")
                line("        return " + gitTime + "L;")
                line("    }")
                line()
                // public static long getGitHash() {
                //     return "749dda081c3e7862050255817bc239b9255b1582";
                // }
                line("    public static String getGitHash() {")
                line("        return \"$gitHash\";")
                line("    }")
                line()
                // public static String getGitBranch() {
                //     return "master";
                // }
                line("    public static String getGitBranch() {")
                line("        return \"$gitBranch\";")
                line("    }")
                line()
                line("}")
            }
        }.toString()
    }
}

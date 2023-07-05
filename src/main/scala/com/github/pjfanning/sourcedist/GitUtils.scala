package com.github.pjfanning.sourcedist

import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.treewalk.TreeWalk

import java.io.File
import scala.collection.mutable

object GitUtils {
  def lsTree(dir: File): Seq[String] = {
    val lsDir      = dir.getAbsoluteFile
    val gitDir     = findGitDir(lsDir)
    val repository = new FileRepositoryBuilder().setGitDir(gitDir).readEnvironment.findGitDir.build
    val prefix = if (gitDir.getParentFile == null) {
      ""
    } else {
      val start = removeStart(lsDir.getAbsolutePath, gitDir.getParentFile.getAbsolutePath)
      val sep   = File.separator
      if (start.isEmpty) start else s"${removeStart(start, sep)}$sep"
    }
    try
      getRepositoryFileListing(repository, prefix)
    finally
      repository.close()
  }

  // it is assumed that the `dir` file has an absolute path already
  // use dir.getAbsoluteFile if unsure
  private def findGitDir(dir: File): File = {
    val possibleGitDir = new File(dir, ".git")
    if (possibleGitDir.exists()) {
      possibleGitDir
    } else if (dir.getParentFile != null) {
      findGitDir(dir.getParentFile)
    } else {
      throw new IllegalStateException("Failed to find .git dir")
    }
  }

  private def getRepositoryFileListing(repository: Repository, prefix: String): Seq[String] = {
    val head = repository.findRef("HEAD")
    if (head == null) {
      throw new IllegalStateException("cannot find git HEAD")
    }
    val walk     = new RevWalk(repository)
    val commit   = walk.parseCommit(head.getObjectId)
    val tree     = commit.getTree
    val treeWalk = new TreeWalk(repository)
    treeWalk.addTree(tree)
    treeWalk.setRecursive(true)
    val buffer = mutable.Buffer[String]()
    while (treeWalk.next) {
      val path = treeWalk.getPathString
      if (path.startsWith(prefix)) buffer.append(path.substring(prefix.length))
    }
    buffer
  }

  private def removeStart(str: String, remove: String): String =
    if (str.isEmpty || remove.isEmpty) {
      str
    } else if (str.startsWith(remove)) {
      str.substring(remove.length)
    } else {
      str
    }
}

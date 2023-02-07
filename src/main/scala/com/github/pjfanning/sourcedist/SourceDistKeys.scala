package com.github.pjfanning.sourcedist

import sbt.{File, settingKey, taskKey}

trait SourceDistKeys {
  val sourceDistHomeDir =
    settingKey[File]("Home directory which contains the projects sources, defaults to project root directory")
  val sourceDistTargetDir = settingKey[File](
    "Target directory where to create the archives, defaults dist folder under root project target folder"
  )
  val sourceDistVersion =
    settingKey[String]("The version to be used in the output archive file names, defaults to the root projects version")
  val sourceDistName = settingKey[String](
    "The name to be used as the prefix in the output archive file names, defaults to root projects name"
  )
  val sourceDistSuffix = settingKey[String](
    "The suffix to be used in the output archive file names, defaults to today's date in YYMMDD format"
  )
  val sourceDistGenerate = taskKey[Unit]("Generate the source distribution packages")
}

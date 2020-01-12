package com.github.jan222ik.programming.time.evaluator.data

data class Duration (
    val minutes: Long
)

data class ProjectEntry(
    val name: String,
    val duration: Duration
)

data class LanguageEntry(
    val languageName: String,
    val duration: Duration
)

data class IdeEntry(
    val ideName: String,
    val duration: Duration
)

data class OsEntry(
    val osName: String,
    val duration: Duration
)

data class MachineEntry(
    val machineName: String,
    val duration: Duration
)

data class WeekWrapper(
    val startDate: String,
    val endDate: String,
    val total: Duration,
    val projects: List<ProjectEntry>,
    val languages: List<LanguageEntry>,
    val ides: List<IdeEntry>,
    val oss: List<OsEntry>,
    val machines: List<MachineEntry>
)

package com.flownote.data.repository

import com.flownote.data.model.Category
import com.flownote.data.model.Template

/**
 * Repository for note templates
 */
object TemplateRepository {
    
    fun getDefaultTemplates(): List<Template> {
        return listOf(
            Template(
                id = "meeting",
                name = "Meeting Notes",
                description = "Template for meeting notes with agenda and action items",
                content = """
                    <b>Meeting Title:</b> <br><br>
                    <b>Date:</b> <br>
                    <b>Attendees:</b> <br><br>
                    <b>Agenda:</b><br>
                    <ul>
                        <li>Topic 1</li>
                        <li>Topic 2</li>
                        <li>Topic 3</li>
                    </ul>
                    <br>
                    <b>Discussion Notes:</b><br><br>
                    <b>Action Items:</b><br>
                    <ul>
                        <li>Action 1</li>
                        <li>Action 2</li>
                    </ul>
                    <br>
                    <b>Next Steps:</b><br>
                """.trimIndent(),
                category = Category.MEETINGS
            ),
            Template(
                id = "todo",
                name = "To-Do List",
                description = "Simple checklist for tasks",
                content = """
                    <b>To-Do List</b><br><br>
                    <b>Priority Tasks:</b><br>
                    <ul>
                        <li>Task 1</li>
                        <li>Task 2</li>
                        <li>Task 3</li>
                    </ul>
                    <br>
                    <b>Other Tasks:</b><br>
                    <ul>
                        <li>Task 4</li>
                        <li>Task 5</li>
                    </ul>
                """.trimIndent(),
                category = Category.TASKS
            ),
            Template(
                id = "journal",
                name = "Daily Journal",
                description = "Template for daily journaling",
                content = """
                    <b>Daily Journal - [Date]</b><br><br>
                    <b>Today's Highlights:</b><br><br>
                    <b>What went well:</b><br><br>
                    <b>Challenges faced:</b><br><br>
                    <b>Lessons learned:</b><br><br>
                    <b>Tomorrow's goals:</b><br>
                    <ul>
                        <li>Goal 1</li>
                        <li>Goal 2</li>
                        <li>Goal 3</li>
                    </ul>
                    <br>
                    <b>Gratitude:</b><br>
                """.trimIndent(),
                category = Category.GENERAL
            ),
            Template(
                id = "project",
                name = "Project Plan",
                description = "Template for project planning",
                content = """
                    <b>Project Name:</b> <br><br>
                    <b>Objective:</b><br><br>
                    <b>Timeline:</b><br>
                    Start Date: <br>
                    End Date: <br><br>
                    <b>Milestones:</b><br>
                    <ol>
                        <li>Milestone 1</li>
                        <li>Milestone 2</li>
                        <li>Milestone 3</li>
                    </ol>
                    <br>
                    <b>Resources Needed:</b><br><br>
                    <b>Risks:</b><br><br>
                    <b>Success Criteria:</b><br>
                """.trimIndent(),
                category = Category.IDEAS
            ),
            Template(
                id = "blank",
                name = "Blank Note",
                description = "Start with a blank note",
                content = "",
                category = Category.GENERAL
            )
        )
    }
    
    fun getTemplateById(id: String): Template? {
        return getDefaultTemplates().find { it.id == id }
    }
}

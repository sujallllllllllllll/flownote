package com.flownote.util

import android.content.Context
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import com.flownote.data.model.Note
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class PdfExporter {

    /**
     * Export a note to PDF format
     * @param context Application context
     * @param note Note to export
     * @return File object pointing to the exported PDF
     * @throws IOException if export fails
     */
    @Throws(IOException::class)
    fun exportNoteToPdf(context: Context, note: Note): File {
        // Create PDF document
        val pdfDocument = PdfDocument()
        
        // Page settings
        val pageWidth = 595 // A4 width in points (8.27 inches * 72)
        val pageHeight = 842 // A4 height in points (11.69 inches * 72)
        val margin = 40
        val contentWidth = pageWidth - (2 * margin)
        
        // Create paint for text
        val titlePaint = Paint().apply {
            textSize = 24f
            isAntiAlias = true
            color = android.graphics.Color.BLACK
        }
        
        val contentPaint = Paint().apply {
            textSize = 14f
            isAntiAlias = true
            color = android.graphics.Color.DKGRAY
        }
        
        val metaPaint = Paint().apply {
            textSize = 10f
            isAntiAlias = true
            color = android.graphics.Color.GRAY
        }
        
        // Start first page
        var pageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas = page.canvas
        var yPosition = margin.toFloat()
        
        // Draw title
        val title = note.getDisplayTitle()
        yPosition += 30
        canvas.drawText(title, margin.toFloat(), yPosition, titlePaint)
        yPosition += 20
        
        // Draw metadata
        val dateStr = "Created: ${DateUtils.formatDate(note.createdAt)}"
        canvas.drawText(dateStr, margin.toFloat(), yPosition, metaPaint)
        yPosition += 15
        
        val categoryStr = "Category: ${note.category.name}"
        canvas.drawText(categoryStr, margin.toFloat(), yPosition, metaPaint)
        yPosition += 30
        
        // Draw content
        val lines = note.content.split("\n")
        val bounds = Rect()
        
        for (line in lines) {
            // Wrap text if too long
            val words = line.split(" ")
            var currentLine = ""
            
            for (word in words) {
                val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
                contentPaint.getTextBounds(testLine, 0, testLine.length, bounds)
                
                if (bounds.width() > contentWidth && currentLine.isNotEmpty()) {
                    // Draw current line and start new one
                    if (yPosition + 20 > pageHeight - margin) {
                        // Need new page
                        pdfDocument.finishPage(page)
                        pageNumber++
                        pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                        page = pdfDocument.startPage(pageInfo)
                        canvas = page.canvas
                        yPosition = margin.toFloat()
                    }
                    
                    canvas.drawText(currentLine, margin.toFloat(), yPosition, contentPaint)
                    yPosition += 20
                    currentLine = word
                } else {
                    currentLine = testLine
                }
            }
            
            // Draw remaining text
            if (currentLine.isNotEmpty()) {
                if (yPosition + 20 > pageHeight - margin) {
                    // Need new page
                    pdfDocument.finishPage(page)
                    pageNumber++
                    pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                    page = pdfDocument.startPage(pageInfo)
                    canvas = page.canvas
                    yPosition = margin.toFloat()
                }
                
                canvas.drawText(currentLine, margin.toFloat(), yPosition, contentPaint)
                yPosition += 20
            }
        }
        
        // Finish last page
        pdfDocument.finishPage(page)
        
        // Save to file
        val outputDir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ - use scoped storage
            File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "FlowNotes")
        } else {
            // Older versions - use Downloads
            @Suppress("DEPRECATION")
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "FlowNotes")
        }
        
        if (!outputDir.exists()) {
            outputDir.mkdirs()
        }
        
        val fileName = sanitizeFileName(title) + "_" + System.currentTimeMillis() + ".pdf"
        val outputFile = File(outputDir, fileName)
        
        FileOutputStream(outputFile).use { outputStream ->
            pdfDocument.writeTo(outputStream)
        }
        
        pdfDocument.close()
        
        return outputFile
    }
    
    /**
     * Sanitize file name by removing invalid characters
     */
    private fun sanitizeFileName(name: String): String {
        return name.replace(Regex("[^a-zA-Z0-9._-]"), "_")
            .take(50) // Limit length
            .ifEmpty { "note" }
    }
}

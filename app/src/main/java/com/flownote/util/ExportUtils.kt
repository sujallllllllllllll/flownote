package com.flownote.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import android.text.StaticLayout
import android.text.TextPaint
import androidx.core.content.FileProvider
import com.flownote.data.model.Note
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object ExportUtils {

    fun exportAsText(context: Context, note: Note) {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "${note.title}\n\n${note.getPlainTextContent()}")
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Share Note")
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(shareIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun exportAsPdf(context: Context, note: Note) {
        val pdfDocument = PdfDocument()
        val pageWidth = 595
        val pageHeight = 842
        val margin = 40f
        val contentWidth = pageWidth - (2 * margin.toInt())
        
        var currentPage = 1
        var yPosition = 120f // Start below header
        
        // Create first page
        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPage).create()
        var page = pdfDocument.startPage(pageInfo)
        var canvas: Canvas = page.canvas
        
        val paint = TextPaint()
        paint.color = Color.BLACK
        paint.textSize = 12f
        
        val boldPaint = TextPaint(paint)
        boldPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        
        val italicPaint = TextPaint(paint)
        italicPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
        
        // Draw header on first page
        drawPageHeader(canvas, note.title, margin)
        
        // Parse HTML content and render with formatting
        val htmlContent = note.content
        val parsedContent = parseHtmlForPdf(htmlContent)
        
        for (block in parsedContent) {
            // Check if we need a new page
            if (yPosition > pageHeight - 100) {
                // Draw footer on current page
                drawPageFooter(canvas, currentPage, pageWidth.toFloat(), pageHeight.toFloat())
                pdfDocument.finishPage(page)
                
                // Start new page
                currentPage++
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, currentPage).create()
                page = pdfDocument.startPage(pageInfo)
                canvas = page.canvas
                yPosition = 60f // Reset position for new page
            }
            
            when (block.type) {
                ContentType.TEXT -> {
                    val textPaint = when {
                        block.isBold && block.isItalic -> {
                            val p = TextPaint(paint)
                            p.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC)
                            p
                        }
                        block.isBold -> boldPaint
                        block.isItalic -> italicPaint
                        else -> paint
                    }
                    
                    val layout = StaticLayout.Builder.obtain(
                        block.text,
                        0,
                        block.text.length,
                        textPaint,
                        contentWidth
                    )
                    .setAlignment(android.text.Layout.Alignment.ALIGN_NORMAL)
                    .setLineSpacing(1.0f, 1.2f)
                    .build()
                    
                    canvas.save()
                    canvas.translate(margin, yPosition)
                    layout.draw(canvas)
                    canvas.restore()
                    
                    yPosition += layout.height + 10f
                }
                ContentType.BULLET_ITEM -> {
                    // Draw bullet point
                    canvas.drawCircle(margin + 10f, yPosition + 5f, 3f, paint)
                    
                    val layout = StaticLayout.Builder.obtain(
                        block.text,
                        0,
                        block.text.length,
                        paint,
                        contentWidth - 30
                    )
                    .setAlignment(android.text.Layout.Alignment.ALIGN_NORMAL)
                    .setLineSpacing(1.0f, 1.2f)
                    .build()
                    
                    canvas.save()
                    canvas.translate(margin + 25f, yPosition)
                    layout.draw(canvas)
                    canvas.restore()
                    
                    yPosition += layout.height + 8f
                }
                ContentType.NUMBERED_ITEM -> {
                    // Draw number
                    canvas.drawText("${block.number}.", margin + 10f, yPosition + 12f, paint)
                    
                    val layout = StaticLayout.Builder.obtain(
                        block.text,
                        0,
                        block.text.length,
                        paint,
                        contentWidth - 40
                    )
                    .setAlignment(android.text.Layout.Alignment.ALIGN_NORMAL)
                    .setLineSpacing(1.0f, 1.2f)
                    .build()
                    
                    canvas.save()
                    canvas.translate(margin + 35f, yPosition)
                    layout.draw(canvas)
                    canvas.restore()
                    
                    yPosition += layout.height + 8f
                }
            }
        }
        
        // Draw footer on last page
        drawPageFooter(canvas, currentPage, pageWidth.toFloat(), pageHeight.toFloat())
        pdfDocument.finishPage(page)

        // Save file
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "${note.title.ifBlank { "note" }}_${System.currentTimeMillis()}.pdf")
        
        try {
            pdfDocument.writeTo(FileOutputStream(file))
            shareFile(context, file, "application/pdf")
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            pdfDocument.close()
        }
    }
    
    private fun drawPageHeader(canvas: Canvas, title: String, margin: Float) {
        val titlePaint = TextPaint()
        titlePaint.color = Color.BLACK
        titlePaint.textSize = 18f
        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        
        val datePaint = TextPaint()
        datePaint.color = Color.GRAY
        datePaint.textSize = 10f
        
        // Draw title
        canvas.drawText(title.ifBlank { "Untitled Note" }, margin, 50f, titlePaint)
        
        // Draw date
        val dateStr = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(java.util.Date())
        canvas.drawText(dateStr, margin, 70f, datePaint)
        
        // Draw separator line
        val linePaint = Paint()
        linePaint.color = Color.LTGRAY
        linePaint.strokeWidth = 1f
        canvas.drawLine(margin, 85f, 595f - margin, 85f, linePaint)
    }
    
    private fun drawPageFooter(canvas: Canvas, pageNumber: Int, pageWidth: Float, pageHeight: Float) {
        val footerPaint = TextPaint()
        footerPaint.color = Color.GRAY
        footerPaint.textSize = 10f
        footerPaint.textAlign = Paint.Align.CENTER
        
        canvas.drawText("Page $pageNumber", pageWidth / 2, pageHeight - 30f, footerPaint)
    }
    
    private fun parseHtmlForPdf(html: String): List<ContentBlock> {
        val blocks = mutableListOf<ContentBlock>()
        
        // Simple HTML parser for basic tags
        var content = html
            .replace("&nbsp;", " ")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&amp;", "&")
        
        // Parse lists
        val ulRegex = Regex("<ul>(.*?)</ul>", RegexOption.DOT_MATCHES_ALL)
        val olRegex = Regex("<ol>(.*?)</ol>", RegexOption.DOT_MATCHES_ALL)
        val liRegex = Regex("<li>(.*?)</li>", RegexOption.DOT_MATCHES_ALL)
        
        // Extract unordered lists
        ulRegex.findAll(content).forEach { match ->
            val listContent = match.groupValues[1]
            liRegex.findAll(listContent).forEach { item ->
                val text = item.groupValues[1].replace(Regex("<[^>]*>"), "").trim()
                if (text.isNotBlank()) {
                    blocks.add(ContentBlock(ContentType.BULLET_ITEM, text))
                }
            }
            content = content.replace(match.value, "")
        }
        
        // Extract ordered lists
        var itemNumber = 1
        olRegex.findAll(content).forEach { match ->
            val listContent = match.groupValues[1]
            liRegex.findAll(listContent).forEach { item ->
                val text = item.groupValues[1].replace(Regex("<[^>]*>"), "").trim()
                if (text.isNotBlank()) {
                    blocks.add(ContentBlock(ContentType.NUMBERED_ITEM, text, number = itemNumber++))
                }
            }
            content = content.replace(match.value, "")
        }
        
        // Parse remaining text with formatting
        val boldRegex = Regex("<b>(.*?)</b>", RegexOption.DOT_MATCHES_ALL)
        val italicRegex = Regex("<i>(.*?)</i>", RegexOption.DOT_MATCHES_ALL)
        
        // Split by line breaks
        val lines = content.split(Regex("<br>|<br/>|<br />|\\n"))
        for (line in lines) {
            var text = line.replace(Regex("<[^>]*>"), "").trim()
            if (text.isNotBlank()) {
                val isBold = boldRegex.containsMatchIn(line)
                val isItalic = italicRegex.containsMatchIn(line)
                blocks.add(ContentBlock(ContentType.TEXT, text, isBold = isBold, isItalic = isItalic))
            }
        }
        
        return blocks
    }
    
    private data class ContentBlock(
        val type: ContentType,
        val text: String,
        val isBold: Boolean = false,
        val isItalic: Boolean = false,
        val number: Int = 0
    )
    
    private enum class ContentType {
        TEXT, BULLET_ITEM, NUMBERED_ITEM
    }

    private fun shareFile(context: Context, file: File, mimeType: String) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val chooser = Intent.createChooser(intent, "Share Export")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        
        try {
            context.startActivity(chooser)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

package com.example.utils

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.example.data.local.Assessment
import com.example.data.local.Patient
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfService {

    fun generateAndSharePdf(context: Context, patient: Patient, assessment: Assessment) {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size in PostScript points
        val page = document.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val paint = Paint()

        // Background
        paint.color = Color.WHITE
        canvas.drawPaint(paint)

        // Header
        paint.color = android.graphics.Color.parseColor("#9B6CBA") // Primary Color
        paint.textSize = 24f
        paint.isFakeBoldText = true
        canvas.drawText("Pilates Espaço Mulher", 50f, 60f, paint)
        
        paint.color = Color.BLACK
        paint.textSize = 14f
        paint.isFakeBoldText = false
        canvas.drawText("Dra. Rogéria Collares — CREFITO 23093-F", 50f, 85f, paint)
        canvas.drawText("Costa Azul, Rio das Ostras | (22) 99947-4304", 50f, 105f, paint)

        // Divider
        paint.color = Color.LTGRAY
        canvas.drawLine(50f, 120f, 545f, 120f, paint)

        // Title
        paint.color = android.graphics.Color.parseColor("#6A1B15") // Accent Color
        paint.textSize = 18f
        paint.isFakeBoldText = true
        canvas.drawText("Ficha de Avaliação Clínica", 50f, 160f, paint)

        // Patient Details
        paint.color = Color.BLACK
        paint.textSize = 12f
        paint.isFakeBoldText = false
        var yPos = 190f
        val lineSpacing = 20f
        
        val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(assessment.evaluationDate))
        
        canvas.drawText("Data da Avaliação: \$dateStr", 50f, yPos, paint); yPos += lineSpacing
        canvas.drawText("Nome: \${patient.name}", 50f, yPos, paint); yPos += lineSpacing
        canvas.drawText("Idade: \${patient.age} | Contato: \${patient.phone}", 50f, yPos, paint); yPos += lineSpacing
        
        // Assessment Summary (Mock logic for space constraints)
        yPos += 10f
        paint.isFakeBoldText = true
        canvas.drawText("Bioimpedância", 50f, yPos, paint); yPos += lineSpacing
        paint.isFakeBoldText = false
        canvas.drawText("Peso: \${assessment.weight}kg | Altura: \${assessment.height}m", 50f, yPos, paint); yPos += lineSpacing
        canvas.drawText("IMC: \${assessment.bmi} | Gordura: \${assessment.bodyFat}%", 50f, yPos, paint); yPos += lineSpacing
        
        yPos += 10f
        paint.isFakeBoldText = true
        canvas.drawText("Análise Profissional:", 50f, yPos, paint); yPos += lineSpacing
        paint.isFakeBoldText = false
        val words = assessment.professionalAnalysis.split(" ")
        var line = ""
        for (word in words) {
            if (paint.measureText(line + word) > 495f) {
                canvas.drawText(line, 50f, yPos, paint)
                yPos += lineSpacing
                line = "\$word "
            } else {
                line += "\$word "
            }
        }
        if (line.isNotEmpty()) {
            canvas.drawText(line, 50f, yPos, paint)
            yPos += lineSpacing
        }
        
        // Footer signature
        yPos = 750f
        paint.color = Color.LTGRAY
        canvas.drawLine(150f, yPos, 445f, yPos, paint)
        paint.color = Color.BLACK
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("Assinatura Profissional", 297f, yPos + 20f, paint)

        document.finishPage(page)

        // Save to cache and share
        try {
            val safeName = patient.name.replace(' ', '_')
            val file = File(context.cacheDir, "Avaliacao_$safeName.pdf")
            document.writeTo(FileOutputStream(file))
            document.close()
            sharePdf(context, file)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun sharePdf(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(context, "\${context.packageName}.fileprovider", file)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(intent, "Compartilhar Avaliação"))
    }
}

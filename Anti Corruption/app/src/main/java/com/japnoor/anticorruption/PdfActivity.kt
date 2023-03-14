package com.japnoor.anticorruption

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.japnoor.anticorruption.databinding.ActivityPdfBinding
import com.shockwave.pdfium.PdfDocument
import com.shockwave.pdfium.PdfDocument.Bookmark

class PdfActivity : AppCompatActivity(), OnPageChangeListener, OnLoadCompleteListener {

    lateinit var binding: ActivityPdfBinding
    var pageNumber=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val scrollHandle = binding.pdfView.fromAsset("tele.pdf").defaultPage(pageNumber)
            .enableSwipe(true)
            .swipeHorizontal(true)
            .onPageChange(this@PdfActivity)
            .enableAnnotationRendering(true)
            .onLoad(this)
            .load()

    }

    override fun onPageChanged(page: Int, pageCount: Int) {
        pageNumber = page;
        binding.name.setText(String.format("%s %s / %s", "Telephone Numbers", page + 1, pageCount))
    }

    override fun loadComplete(nbPages: Int) {
        val meta: PdfDocument.Meta = binding.pdfView.getDocumentMeta()
        printBookmarksTree(binding.pdfView.getTableOfContents(), "-")
    }

    fun printBookmarksTree(tree: List<Bookmark>, sep: String) {
        for (b in tree) {
            if (b.hasChildren()) {
                printBookmarksTree(b.children, "$sep-")
            }
        }
    }
}
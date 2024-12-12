package com.capstone.antidot.ui

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.capstone.antidot.R
import com.capstone.antidot.api.models.ArticleItem

class NewsAdapter(private val articleList: List<ArticleItem>) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvItemName: TextView = itemView.findViewById(R.id.tvItemNameNews)
        val tvItemDescription: Button = itemView.findViewById(R.id.btnNews)  // Button untuk URL
        val headerImage: ImageView = itemView.findViewById(R.id.header_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val articleItem = articleList[position]
        holder.tvItemName.text = articleItem.title

        // Jangan mengubah teks tombol, biarkan teks default yang ada di layout XML
        holder.tvItemDescription.setOnClickListener {
            // Mengarahkan ke URL artikel
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(articleItem.url))
            holder.itemView.context.startActivity(intent)  // Akses context dengan holder.itemView
        }

        // Memuat gambar dengan Glide
        Glide.with(holder.itemView.context).load(articleItem.image).into(holder.headerImage)
    }

    override fun getItemCount(): Int = articleList.size
}

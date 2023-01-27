package com.example.burmeseforkids.adapters

import android.content.Context
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.burmeseforkids.R
import com.example.burmeseforkids.models.Word
import kotlin.properties.Delegates

class WordAdapter(content: Context, resourceId: Int, objects: ArrayList<Word>) :
    ArrayAdapter<Word>(content, resourceId, objects) {

    private var content by Delegates.notNull<Context>()
    private var backgroundColor by Delegates.notNull<Int>()
    private lateinit var mediaPlayer: MediaPlayer

    constructor(
        content: Context,
        resourceId: Int,
        objects: ArrayList<Word>,
        colorResourceId: Int
    ) : this(content, resourceId, objects) {
        this.content = content
        this.backgroundColor = colorResourceId
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        // Check if the existing view is being reused, otherwise inflate the view
        var listItemView = convertView
        if (listItemView == null)
            listItemView =
                LayoutInflater.from(context).inflate(R.layout.list_item_layout, parent, false)

        // Get the object located at current position in the list
        val currentWord = getItem(position)

        // Hook view Ids in the custom layout file to variables
        val burmeseWordTv = listItemView?.findViewById<TextView>(R.id.burmese_word_tv)
        val englishWordTv = listItemView?.findViewById<TextView>(R.id.english_word_tv)
        val imageView = listItemView?.findViewById<ImageView>(R.id.imageView)
        val parentRelative = listItemView?.findViewById<RelativeLayout>(R.id.parentRelative)

        // Set texts on these two text views
        burmeseWordTv?.text = currentWord?.burmeseTrans
        englishWordTv?.text = currentWord?.englishTrans
        // Set image on the image view
        currentWord?.imgDrawable?.let { drawable ->
            imageView?.visibility = View.VISIBLE
            imageView?.setImageDrawable(drawable)
        }
        // Set background color on linear layout
        parentRelative?.setBackgroundColor(backgroundColor)

        return listItemView!!
    }
}
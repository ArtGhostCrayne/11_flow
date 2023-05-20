package ru.netology.nmedia.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.nmedia.dto.Post

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val author: String,
    val authorAvatar: String,
    val content: String,
    val published: String,
    val likedByMe: Boolean,
    val likes: Int = 0,
    val newPost: Boolean = false,
) {
    fun toDto() = Post(id, author, authorAvatar, content, published, likedByMe, likes )

    companion object {
        fun fromDto(dto: Post, newPost: Boolean) =
            PostEntity(dto.id, dto.author, dto.authorAvatar, dto.content, dto.published, dto.likedByMe, dto.likes , newPost = newPost)

    }
}

fun List<PostEntity>.toDto(): List<Post> = map(PostEntity::toDto)
fun List<Post>.toEntity(newPost: Boolean = false): List<PostEntity> = map{
    PostEntity.fromDto(it, newPost)
}
    

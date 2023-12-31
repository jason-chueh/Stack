package com.example.stack.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.stack.data.dataclass.Template

@Dao
interface TemplateDao {
    @Upsert
    fun upsertTemplate(template: Template)

    @Query("SELECT template_id FROM template WHERE user_id = :userId")
    fun searchTemplateIdListByUserId(userId: String): List<String>

    @Query("SELECT * FROM template WHERE user_id = :userId")
    fun searchTemplatesListByUserId(userId: String): List<Template>

    @Query("DELETE FROM template")
    fun deleteAllTemplate()

    @Query("DELETE FROM template WHERE template_id = :templateId")
    fun deleteTemplateByTemplateId(templateId: String)


}
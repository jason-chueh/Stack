package com.example.stack.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.stack.data.dataclass.TemplateExerciseRecord

@Dao
interface TemplateExerciseRecordDao {
    @Upsert
    fun upsertTemplateExerciseRecord(templateExerciseRecords: TemplateExerciseRecord)

    @Upsert
    fun upsertTemplateExerciseRecord(templateExerciseRecordsList: List<TemplateExerciseRecord>)

    @Query("SELECT * FROM template_exercise_records WHERE template_id = :templateId")
    fun getTemplateExerciseRecordListByTemplateId(templateId: String): List<TemplateExerciseRecord>

    @Query("DELETE FROM template_exercise_records")
    fun deleteAllTemplateExerciseRecords()
}
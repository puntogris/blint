package com.puntogris.blint.data.local.dao

import androidx.room.*
import com.puntogris.blint.model.Employee

@Dao
interface EmployeeDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(employee: Employee)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(employees: List<Employee>)

    @Query("DELETE FROM employee")
    suspend fun deleteAll()

    @Transaction
    suspend fun syncEmployees(employees: List<Employee>){
        deleteAll()
        insert(employees)
    }

    @Query("DELETE FROM employee where businessId = :businessId")
    suspend fun deleteEmployeeWithBusinessId(businessId: String)

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM employee INNER JOIN user ON employeeId = currentUid WHERE userId = '1'")
    suspend fun getEmployeesList(): List<Employee>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT businessId FROM employee INNER JOIN user ON employeeId = currentUid WHERE userId = '1'")
    suspend fun getBusinessIdsList(): List<String>

    @Query("SELECT * FROM employee WHERE businessId = :businessId LIMIT 1")
    suspend fun getEmployeeWithBusinessId(businessId: String): Employee
}
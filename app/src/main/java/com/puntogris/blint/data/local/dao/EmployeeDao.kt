package com.puntogris.blint.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.puntogris.blint.model.BusinessItem
import com.puntogris.blint.model.Employee

@Dao
interface EmployeeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(employee: Employee)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(employees: List<Employee>)

    @Query("DELETE FROM employee")
    suspend fun deleteAll()

    @Transaction
    suspend fun syncEmployees(employees: List<Employee>){
        deleteAll()
        insert(employees)
    }

    @Query("DELETE FROM employee where businessId = :businessId")
    suspend fun deleteBusiness(businessId: String)

    @Update
    suspend fun update(employee: Employee)

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM employee INNER JOIN roomuser ON employeeId = currentUid WHERE userId = '1'")
    suspend fun getEmployeesList(): List<Employee>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM employee INNER JOIN roomuser ON employeeId = currentUid WHERE userId = '1'")
    fun getEmployeesListLiveData(): LiveData<List<Employee>>

    @Query("SELECT COUNT(*) FROM employee INNER JOIN roomuser ON employeeId = currentUid WHERE userId = '1'")
    suspend fun getCount(): Int

    @Query("SELECT role FROM employee INNER JOIN roomuser ON employeeId = currentUid WHERE userId = '1' AND employeeId = :employeeId")
    suspend fun getBusinessUserRole(employeeId: String):String

}
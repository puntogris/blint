package com.puntogris.blint.ui.settings

import androidx.lifecycle.ViewModel
import com.puntogris.blint.data.repo.employees.EmployeesRepository
import com.puntogris.blint.data.repo.tickets.TicketsRepository
import com.puntogris.blint.model.Employee
import com.puntogris.blint.model.Ticket
import com.puntogris.blint.utils.SimpleResult
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TicketsViewModel @Inject constructor(
    private val ticketsRepository: TicketsRepository,
    private val employeesRepository: EmployeesRepository
):ViewModel() {

    private val ticket = Ticket()

    suspend fun getEmployeeList() = employeesRepository.getEmployeeListRoom()

    suspend fun sendTicket(message:String):SimpleResult {
        ticket.message = message
        return ticketsRepository.sendTicketDatabase(ticket)
    }

    fun updateTicketBusiness(employee: Employee){
        ticket.apply {
            businessId = employee.businessId
            employeeId = employee.employeeId
            businessType = employee.businessType
            businessStatus = employee.businessStatus
            businessOwner = employee.businessOwner
        }
    }
}
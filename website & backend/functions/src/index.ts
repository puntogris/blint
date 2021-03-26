import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin'
admin.initializeApp()


export const sendJoinedEmployeeNotifications = functions.firestore.document('users/{userId}/business/{businessId}/employees/{employeeId}').onCreate(async(snap, context) =>{
    try {
        const ownerId = context.params.get('ownerId')
        const businessId = context.params.get('businessId')
        const employeeName = snap.get('name')
        const employeeId = snap.get('employeeId')
        //to send a notification with how to start using the app and welcome
        //the new user into the business
        const newEmployeeNotif = {
            type: 'WELCOME_EMPLOYEE_NOTIFICATION',
            wasRead: false,
            timestamp : admin.firestore.FieldValue.serverTimestamp(),
        }

        //to welcome the new user
        const currentEmployeesNotif = {
            type: 'WELCOME_EMPLOYEE_BUSINESS_NOTIFICATION',
            wasRead: false,
            timestamp : admin.firestore.FieldValue.serverTimestamp(),
            employeeName: employeeName
        }

        const batch = admin.firestore().batch()

        const employees = await admin.firestore().collection('users').doc(ownerId).collection('business').doc(businessId).collection('employees').get()

        employees.forEach(employee => {
            const notificationRef = admin.firestore().collection('users').doc(employee.get('employeeId')).collection('notifications').doc()

            if(employee.get('employeeId') == employeeId){
                batch.set(notificationRef, newEmployeeNotif)
            }else {
                batch.set(notificationRef, currentEmployeesNotif)
            }
        })
        return batch.commit()
    } catch (error) {
        return error
    }
})

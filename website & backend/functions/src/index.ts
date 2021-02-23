import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin'
admin.initializeApp()


export const createContactRequestChilds = functions.firestore.document('employee_requests/{requestId}').onCreate(async(snap, context) =>{
    const requestId = context.params.requestId
    const employeeId = snap.get("employeeId")
    const businessName = snap.get("businessName")
    const businessId = snap.get("businessId")
    const role = snap.get("role")
    const ownerId = snap.get("ownerId")
    const businessTimestamp = snap.get("businessTimestamp")
   
    try {
        const employeeDoc = await admin.firestore().collection('users').doc(employeeId).get()
        const employeeName = employeeDoc.get("name")

        const newEmployeeRef = admin.firestore().collection('users')

        const employeeEntry = {
            businessId: businessId,
            employeeId: employeeId,
            name: employeeName,
            businessName: businessName,
            businessType: "ONLINE",
            owner: ownerId,
            role: role,
            employeeTimestamp: admin.firestore.FieldValue.serverTimestamp(),
            businessTimestamp: businessTimestamp,
            parentRequestId: requestId,
            requestState: "PENDING"
        }

        const ownerNotification = {
            email: employeeDoc.get("email"),
            type: "EMPLOYEE_NOTIFICATION_SENT"
        }

        const employeeNotification = {
            businessName: businessName,
            type: "EMPLOYEE_NOTIFICATION_RECEIVED",
            businessId: newEmployeeRef.id
        }

        await newEmployeeRef.doc(ownerId).collection('business').doc(businessId).collection('employees').doc().set(employeeEntry)
        await admin.firestore().collection('users').doc(ownerId).collection('notifications').doc().set(ownerNotification)
        await admin.firestore().collection('users').doc(employeeId).collection('notifications').doc().set(employeeNotification)

        return true
    } catch (error) {
        return error
    }
})

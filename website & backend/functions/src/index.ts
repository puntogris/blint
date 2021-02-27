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

        const newEmployeeRef = admin.firestore().collection('users').doc(ownerId).collection('business').doc(businessId).collection('employees').doc()
        const ownerNotificationRef = admin.firestore().collection('users').doc(ownerId).collection('notifications').doc()
        const employeeNotificationRef = admin.firestore().collection('users').doc(employeeId).collection('notifications').doc()
        const requestReceivedRef = admin.firestore().collection('users').doc(employeeId).collection('requests_received').doc()
        const requestSentRef = admin.firestore().collection('users').doc(ownerId).collection('requests_sent').doc()

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
            id: ownerNotificationRef.id,
            employeeEmail: employeeDoc.get("email"),
            type: "EMPLOYMENT_REQUEST_SENT_NOTIFICATION",
            wasRead: false,
            businessName: businessName,
            timestamp: admin.firestore.FieldValue.serverTimestamp()
        }

        const employeeNotification = {
            id: employeeNotificationRef.id,
            businessName: businessName,
            parentRequestId: requestId,
            type: "EMPLOYMENT_REQUEST_RECEIVED_NOTIFICATION",
            businessId: newEmployeeRef.id,
            wasRead: false,
            timestamp: admin.firestore.FieldValue.serverTimestamp()
        }

        const requestReceived = {
            parentRequestId: requestId,

        }

        const requestSent = {
            parentRequestId: requestId,
        }

        const batch = admin.firestore().batch()

        batch.set(newEmployeeRef, employeeEntry)
        batch.set(ownerNotificationRef, ownerNotification)
        batch.set(employeeNotificationRef, employeeNotification)
        batch.set(requestReceivedRef, requestReceived)
        batch.set(requestSentRef, requestSent)

        await batch.commit()

        return true
    } catch (error) {
        return error
    }
})

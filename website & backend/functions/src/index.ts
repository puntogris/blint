import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin'

admin.initializeApp()

/**
 * It sends a welcoming notification to all the employess of a business when a new employee joins.
 */
export const sendBusinessEmployeesNewEmployeeNotification = functions.firestore.document('users/{userId}/business/{businessId}/employees/{employeeId}').onCreate(async(snap, context) =>{
    try {
        const ownerId = context.params.get('ownerId')
        const businessId = context.params.get('businessId')
        const employeeName = snap.get('name')

        const batch = admin.firestore().batch()

        const employees = await admin.firestore().collection('users').doc(ownerId).collection('business').doc(businessId).collection('employees').get()

        employees.forEach(employee => {
            const notificationRef = admin.firestore().collection('users').doc(employee.get('employeeId')).collection('notifications').doc()

            const notification = {
                id: notificationRef.id,
                message: employeeName,
                timestamp: admin.firestore.FieldValue.serverTimestamp(),
                wasRead: false,
                type: "NEW_EMPLOYEE"
            }
            batch.set(notificationRef, notification)
        })
        return batch.commit()
    } catch (error) {
        return error
    }
})

/**
 * It sends a welcoming notification when a new user joins.
 */
export const sendNewUserNotification = functions.firestore.document('users/{userId}').onCreate(async(snap, context) =>{
    const userId = context.params.userId
    const ref = admin.firestore().collection("users").doc(userId).collection("notifications").doc()
    const notification = {
        id: ref.id,
        imageUri: "",
        timestamp: admin.firestore.FieldValue.serverTimestamp(),
        wasRead: false,
        navigationUri: "https://blint.app",
        type: "NEW_USER"
    }
    return ref.set(notification)
})

/**
 * It sends a notification to the owner when it creates a new business.
 */
export const sendNewBusinessNotification = functions.firestore.document('users/{userId}/business/{businessId}').onCreate(async(snap, context) =>{
    const userId = context.params.userId
    const ref = admin.firestore().collection("users").doc(userId).collection("notifications").doc()
    const notification = {
        id: ref.id,
        imageUri: "",
        timestamp: admin.firestore.FieldValue.serverTimestamp(),
        wasRead: false,
        navigationUri: "https://blint.app",
        type: "NEW_BUSINESS"
    }
    return ref.set(notification)
})

/**
 * Creates a new field when a new client its create to query if we need to search by name.
 */
export const createSearchArrayForNewClient = functions.firestore.document('users/{userId}/business/{businessId}/clients/{clientId}').onCreate(async(snap, context) =>{
    const name = snap.get("name")
    const businessId = context.params.businessId
    const userId = context.params.userId
    const clientId = context.params.clientId
    
    return admin.firestore().collection('users').doc(userId).collection('business').doc(businessId).collection('clients').doc(clientId).update({
        "search_name": getArrayForNameSearch(name)
    })
})

/**
 * Creates a new field when a new supplier its create to query if we need to search by name.
 */
export const createSearchArrayForNewSupplier = functions.firestore.document('users/{userId}/business/{businessId}/suppliers/{supplierId}').onCreate(async(snap, context) =>{
    const name = snap.get("companyName")
    const businessId = context.params.businessId
    const userId = context.params.userId
    const supplierId = context.params.supplierId
    
    return admin.firestore().collection('users').doc(userId).collection('business').doc(businessId).collection('suppliers').doc(supplierId).update({
        "search_name": getArrayForNameSearch(name)
    })
})

/**
 * Creates a array with various combinations of a name to make it posible to query in firestore.
 */
function getArrayForNameSearch(name: string): string[]{
    const list:string[] = []
    
    const process = (character:string, index:number) => {
      if(character !== ' '){
          for(let i = 1; i <= name.length - index ; i++){
            list.push(name.substring(index, index + i))
          }
      }
    }
    [...name].forEach(process)
    return list
}

/**
 * When a business status changes, this will change thier employees status.
 */
export const onBusinessStatusChanged = functions.firestore.document('users/{userId}/business/{businessId}').onUpdate(async(snap, context) =>{
    const businessId = context.params.businessId
    const userId = context.params.userId
    const status = snap.after.get("status")
    const statusTimestamp = snap.after.get("lastStatusTimestamp")

    return admin.firestore().runTransaction(async(t) => {
        let ref = admin.firestore().collection("users").doc(userId).collection("business").doc(businessId).collection("employees")
        let data = await t.get(ref)
        data.forEach(doc =>{
            t.update(doc.ref,({
                "businessStatus": status,
                "lastStatusTimestamp": statusTimestamp
            }))
        })
    })
})

/**
 * Runs once a day to delete businesses that passed the 2 weeks deactivation.
 */
//mepa que mejor hacer que todos los dias corra pero lo unico que haga es cambiar el ondelete por delete
// y despues en la app si es delete, lo borramos de 
// exports.scheduledFunction = functions.pubsub.schedule('every 2 minutes').onRun(async(context) => {
//     const client = require('firebase-tools')
//     const TWO_WEEKS_IN_SECONDS = 14*24*60*60
//     const dateNow = admin.firestore.Timestamp.now()
//     const twoWeeksTimestamp = new admin.firestore.Timestamp(dateNow.seconds - TWO_WEEKS_IN_SECONDS, 0)
//     const businesses = await admin.firestore().collectionGroup("business").where("status","==", "ON_DELETE").where("lastStatusTimestamp", ">=", twoWeeksTimestamp)

//     await client.firestore
//       .delete(businesses, {
//         project: process.env.GCP_PROJECT,
//         recursive: true,
//         yes: true
//       })
//   })
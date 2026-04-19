import * as admin from "firebase-admin";
import { onDocumentCreated } from "firebase-functions/v2/firestore";

admin.initializeApp();

export const onBookingCreated = onDocumentCreated(
  "bookings/{bookingId}",
  async (event) => {
    const booking = event.data?.data();
    if (!booking) return;

    const tradespersonId = booking.tradespersonId;
    const category = booking.category;
    const clientName = booking.clientName;

    // Get tradesperson's FCM token
    const userDoc = await admin
      .firestore()
      .collection("users")
      .doc(tradespersonId)
      .get();

    const fcmToken = userDoc.data()?.fcmToken;
    if (!fcmToken) return;

    // Send push notification
    await admin.messaging().send({
      token: fcmToken,
      notification: {
        title: "Nouvelle demande de service !",
        body: `${clientName} a besoin d'un service de ${category}`,
      },
      data: {
        bookingId: event.params.bookingId,
        type: "new_booking",
      },
    });
  }
);

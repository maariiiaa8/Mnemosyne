const functions = require("firebase-functions/v2/https");
const { defineSecret } = require("firebase-functions/params");
const Stripe = require("stripe");

const stripeSecret = defineSecret("STRIPE_SECRET");

exports.crearPaymentIntent = functions.onCall(
    { secrets: [stripeSecret] },
    async (request) => {
        if (!request.auth) {
            throw new functions.HttpsError(
                "unauthenticated",
                "Debes estar autenticado para realizar un pago"
            );
        }

        const { amount } = request.data;

        if (!amount || amount <= 0) {
            throw new functions.HttpsError(
                "invalid-argument",
                "El importe no es válido"
            );
        }

        try {
            const stripe = new Stripe(stripeSecret.value());
            const paymentIntent = await stripe.paymentIntents.create({
                amount: Math.round(amount * 100),
                currency: "eur",
                metadata: {
                    uid: request.auth.uid
                }
            });

            return { clientSecret: paymentIntent.client_secret };
        } catch (error) {
            throw new functions.HttpsError("internal", error.message);
        }
    }
);
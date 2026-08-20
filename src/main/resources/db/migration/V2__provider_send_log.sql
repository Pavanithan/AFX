-- Standing in for "actually call SendGrid/Twilio/APNs": the simulated provider
-- endpoints persist what they *would* have sent here instead of making a real
-- network call. Distinct from delivery_attempts, which records the outcome
-- (SENT/FAILED, latency, error) as seen from the notification job's side.
CREATE TABLE provider_send_log (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    channel       VARCHAR(20) NOT NULL,
    recipient     VARCHAR(320) NOT NULL,
    message_body  TEXT NOT NULL,
    sent_at       TIMESTAMPTZ NOT NULL DEFAULT now()
);

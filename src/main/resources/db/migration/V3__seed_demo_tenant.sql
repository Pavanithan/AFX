-- The spec has no "create tenant" API (tenantId is just a form field on campaign
-- creation), so a demo tenant is seeded with a fixed, well-known id for anyone
-- exercising the API by hand or in tests.
INSERT INTO tenants (id, name, monthly_message_limit, monthly_campaign_limit)
VALUES ('11111111-1111-1111-1111-111111111111', 'Demo Tenant', 1000000, 1000);

CREATE TABLE notifications (
    id UUID PRIMARY KEY,
    recipient_role VARCHAR(80) NOT NULL,
    recipient_user_id UUID,
    type VARCHAR(80) NOT NULL,
    title VARCHAR(180) NOT NULL,
    body VARCHAR(1000) NOT NULL,
    read BOOLEAN NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    read_at TIMESTAMP WITH TIME ZONE,
    version BIGINT NOT NULL
);

CREATE INDEX idx_notifications_recipient_role ON notifications(recipient_role);
CREATE INDEX idx_notifications_recipient_user ON notifications(recipient_user_id);
CREATE INDEX idx_notifications_read ON notifications(read);

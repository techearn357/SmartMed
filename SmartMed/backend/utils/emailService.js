const nodemailer = require('nodemailer');

/**
 * Creates Nodemailer transporter if valid SMTP credentials are configured in .env.
 */
const createTransporter = () => {
    const user = process.env.EMAIL_USER;
    const pass = process.env.EMAIL_PASS;
    const service = process.env.EMAIL_SERVICE || 'gmail';

    if (user && pass && !user.includes('demo@') && pass !== 'demopassword123') {
        return nodemailer.createTransport({
            service,
            auth: { user, pass }
        });
    }
    return null;
};

/**
 * Sends Email notification via Nodemailer SMTP or prints to console during demo/testing.
 */
const sendEmail = async (to, subject, text, html) => {
    console.log(`\n======================================================`);
    console.log(`📧 [EMAIL NOTIFICATION]`);
    console.log(`TO:      ${to}`);
    console.log(`SUBJECT: ${subject}`);
    console.log(`MESSAGE: ${text}`);
    console.log(`======================================================\n`);

    try {
        const transporter = createTransporter();
        if (transporter) {
            const mailOptions = {
                from: `"SmartMed Reminder Care" <${process.env.EMAIL_USER}>`,
                to,
                subject,
                text,
                html: html || `<div style="font-family: Arial, sans-serif; padding: 20px; line-height: 1.6;">
                    <h2 style="color: #1E88E5;">SmartMed Medication Care</h2>
                    <p>${text.replace(/\n/g, '<br/>')}</p>
                </div>`
            };
            const info = await transporter.sendMail(mailOptions);
            console.log(`[SMTP SUCCESS] Sent email to ${to} (MessageId: ${info.messageId})`);
            return true;
        } else {
            console.log(`[EMAIL NOTICE] Using Console Email Logger. Set real EMAIL_USER & EMAIL_PASS in backend/.env to deliver live emails to inbox.`);
        }
    } catch (err) {
        console.error(`[SMTP ERROR] Failed to send email to ${to}: ${err.message}`);
    }
    return true;
};

module.exports = { sendEmail };

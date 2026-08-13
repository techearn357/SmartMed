const mongoose = require('mongoose');

const connectDB = async () => {
    try {
        const conn = await mongoose.connect(process.env.MONGODB_URI || 'mongodb://localhost:27017/smartmed', {
            serverSelectionTimeoutMS: 2500
        });
        console.log(`MongoDB Connected: ${conn.connection.host}`);
    } catch (error) {
        console.error(`MongoDB Primary Connection Error: ${error.message}`);
        console.log('Attempting fallback to In-Memory MongoDB Server...');
        try {
            const { MongoMemoryServer } = require('mongodb-memory-server');
            const mongoServer = await MongoMemoryServer.create();
            const uri = mongoServer.getUri();
            const conn = await mongoose.connect(uri);
            console.log(`In-Memory MongoDB Connected successfully at: ${conn.connection.host}`);
        } catch (fallbackError) {
            console.error(`In-Memory Fallback failed: ${fallbackError.message}`);
        }
    }
};

module.exports = connectDB;


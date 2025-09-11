const crypto = require('crypto');
const QRCode = require('qrcode');

// QR Code Service configuration (matching the Android app)
const QR_SECRET_KEY = "BusTracker2024";
const QR_CODE_SIZE = 512;

/**
 * Generate SHA-256 hash (matching Android implementation)
 */
function generateHash(data) {
    return crypto.createHash('sha256').update(data).digest('hex');
}

/**
 * Create secure QR code data for bus assignment
 */
function createBusQRData(busId, busNumber, routeId) {
    const timestamp = Date.now();
    const data = `BUS|${busId}|${busNumber}|${routeId}|${timestamp}`;
    const hash = generateHash(data + QR_SECRET_KEY);
    return data + "|" + hash;
}

/**
 * Generate QR code for bus assignment
 */
async function generateBusQRCode(busId, busNumber, routeId) {
    try {
        const qrData = createBusQRData(busId, busNumber, routeId);
        console.log(`Generated QR data: ${qrData}`);
        
        // Generate QR code as PNG
        const qrCodeBuffer = await QRCode.toBuffer(qrData, {
            type: 'png',
            width: QR_CODE_SIZE,
            margin: 2,
            color: {
                dark: '#000000',
                light: '#FFFFFF'
            },
            errorCorrectionLevel: 'H'
        });
        
        // Save QR code to file
        const fs = require('fs');
        const filename = `bus_${busNumber}_qr.png`;
        fs.writeFileSync(filename, qrCodeBuffer);
        console.log(`QR code saved as: ${filename}`);
        
        return qrData;
    } catch (error) {
        console.error('Error generating QR code:', error);
        return null;
    }
}

/**
 * Validate bus QR code data (for testing)
 */
function validateBusQRCode(qrData) {
    try {
        const parts = qrData.split('|');
        if (parts.length !== 6) {
            console.log('Invalid QR code format');
            return null;
        }
        
        const type = parts[0];
        if (type !== 'BUS') {
            console.log('Not a bus QR code');
            return null;
        }
        
        const busId = parts[1];
        const busNumber = parts[2];
        const routeId = parts[3];
        const timestamp = parseInt(parts[4]);
        const hash = parts[5];
        
        // Validate hash
        const dataWithoutHash = `BUS|${busId}|${busNumber}|${routeId}|${timestamp}`;
        const expectedHash = generateHash(dataWithoutHash + QR_SECRET_KEY);
        
        if (hash !== expectedHash) {
            console.log('QR code hash validation failed');
            return null;
        }
        
        // Check if QR code is not too old (within 7 days)
        const currentTime = Date.now();
        if (currentTime - timestamp > 7 * 24 * 60 * 60 * 1000) {
            console.log('QR code is too old');
            return null;
        }
        
        return {
            busId,
            busNumber,
            routeId,
            timestamp
        };
    } catch (error) {
        console.error('Error validating bus QR code:', error);
        return null;
    }
}

// Sample bus data
const sampleBuses = [
    { busId: 'BUS001', busNumber: 'B001', routeId: 'ROUTE001' },
    { busId: 'BUS002', busNumber: 'B002', routeId: 'ROUTE002' },
    { busId: 'BUS003', busNumber: 'B003', routeId: 'ROUTE003' },
    { busId: 'BUS004', busNumber: 'B004', routeId: 'ROUTE004' },
    { busId: 'BUS005', busNumber: 'B005', routeId: 'ROUTE005' }
];

async function generateAllBusQRCodes() {
    console.log('Generating QR codes for sample buses...\n');
    
    for (const bus of sampleBuses) {
        console.log(`Generating QR code for Bus ${bus.busNumber}...`);
        const qrData = await generateBusQRCode(bus.busId, bus.busNumber, bus.routeId);
        
        if (qrData) {
            // Test validation
            const validatedData = validateBusQRCode(qrData);
            if (validatedData) {
                console.log(`✓ Valid QR code for Bus ${bus.busNumber}`);
                console.log(`  Bus ID: ${validatedData.busId}`);
                console.log(`  Route ID: ${validatedData.routeId}`);
                console.log(`  Timestamp: ${new Date(validatedData.timestamp).toLocaleString()}\n`);
            } else {
                console.log(`✗ Invalid QR code for Bus ${bus.busNumber}\n`);
            }
        } else {
            console.log(`✗ Failed to generate QR code for Bus ${bus.busNumber}\n`);
        }
    }
    
    console.log('QR code generation completed!');
    console.log('You can now scan these QR codes with the Android app to test bus assignment.');
}

// Run the script
generateAllBusQRCodes().catch(console.error);

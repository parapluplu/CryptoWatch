require('dotenv').load();
const express = require('express');
const MongoClient = require('mongodb').MongoClient,
    ObjectId = require('mongodb').ObjectId;
const configClient = require("cloud-config-client");
const helmet = require('helmet')
const app = express();
const localConfig = require('config');
const cors = require('cors')

app.use(helmet(), cors())
var db;

var profile = "development";
if (process.env.NODE_ENV) {
    profile = process.env.NODE_ENV;
}
configClient.load({
    name: "application",
    endpoint: localConfig.get("config-server-uri"),
    profiles: profile
}).then((config) => {
    const mongoUri = config.get("spring.data.mongodb.uri");
    const mongoDb = config.get("spring.data.mongodb.database");
    console.log("[LOADED] mongodb uri '" + mongoUri + "'");
    console.log("[LOADED] mongodb db '" + mongoDb + "'");

    // Initialize connection once
    MongoClient.connect(mongoUri, function (err, client) {
        if (err) throw err;

        db = client.db(mongoDb);

        // Start the application after the database connection is ready

        app.listen(localConfig.get("port"), function () {
            console.log("messy-aphrodite listening on " + localConfig.get("port"));
        });
    });
});





app.get('/txnSummary', (req, res) => {

    function exchangeInfo(tokenSymbols, field, cb) {
        db.collection("enrichedTransferMessage").aggregate(
            [
                {
                    $match: {
                        "tokenInfo.symbol": { $in: tokenSymbols },
                        blockDate: { $gte: new Date((new Date().getTime() - (7 * 24 * 60 * 60 * 1000))) },
                        [field]: true
                    }
                },
                {
                    $group: {
                        _id: { symbol: "$tokenInfo.symbol" },
                        exchange_sum: { $sum: "$insecureAmount" }
                    }
                }
            ]
        ).toArray(function (err, result) {
            if (err) {
                console.error("Exception while serving request " + req + ": " + err);
                cb(err, null);
            } else {
                cb(null, result);
            }
        });
    }

    const tokenSymbols = req.query.tokenSymbols.split(",");
    console.log(req.query);
    console.log(tokenSymbols);
    db.collection("enrichedTransferMessage").aggregate(
        [
            {
                $match: {
                    "tokenInfo.symbol": { $in: tokenSymbols },
                    blockDate: { $gte: new Date((new Date().getTime() - (7 * 24 * 60 * 60 * 1000))) }
                }
            },
            {
                $group: {
                    _id: { symbol: "$tokenInfo.symbol" },
                    txns: { $sum: 1 }
                }
            }
        ]
    ).toArray(function (err, txInfo) {
        if (err) {
            res.statusCode = 500;
            res.send("Unexpected exception")
            console.error("Exception while serving request " + req + ": " + err);
        } else {
            exchangeInfo(tokenSymbols, "exchange.in", function (err, xInfoIn) {
                if (err) {
                    res.statusCode = 500;
                    res.send("Unexpected exception")
                    console.error("Exception while serving request " + req + ": " + err);
                } else {
                    exchangeInfo(tokenSymbols, "exchange.out", function (err, xInfoOut) {
                        var result = []
                        console.log(xInfoOut)
                        console.log(xInfoIn)
                        txInfo.forEach(row => {
                            console.log(row);
                            const symbol = row["_id"]["symbol"];
                            result.push(row);
                            merge(xInfoIn, symbol, row, "exchange_in");
                            merge(xInfoOut, symbol, row, "exchange_out");
                        });
                        res.send(result);
                    });
                }
            });
        }
    });
});

app.get('/cxSummary', (req, res) => {

    const tokenSymbols = req.query.tokenSymbols.split(",");
    console.log(req.query);
    console.log(tokenSymbols);
    db.collection("triggerEvent").aggregate(
        [
            {
                $match: {
                    "data.symbol": { $in: tokenSymbols },
                    "data.exchange": true,
                    date: { $gte: new Date((new Date().getTime() - (7 * 24 * 60 * 60 * 1000))) }
                }
            },
            {
                $group: {
                    _id: { symbol: "$data.symbol" },
                    count: { $sum: 1 }
                }
            }
        ]
    ).toArray(function (err, result) {
        if (err) {
            res.statusCode = 500;
            res.send("Unexpected exception")
            console.error("Exception while serving request " + req + ": " + err);
        } else {
            res.send(result);
        }
    });
});

function merge(xInfo, symbol, row, field) {
    var val = 0;
    for(let xi of xInfo) {
        if (xi["_id"]["symbol"] == symbol) {
            val = xi["exchange_sum"];
            break;
        }
    }
    row[field] = val;
}


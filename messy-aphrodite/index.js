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
    endpoint : localConfig.get("config-server-uri"),
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

    const tokenSymbols = req.query.tokenSymbols.split(",");
    console.log(req.query);
    console.log(tokenSymbols);
    db.collection("triggerEvent").aggregate(
        [
            {
                $match: {
                    "data.symbol": {$in: tokenSymbols},
                    date: {$gte: new Date((new Date().getTime() - (7 * 24 * 60 * 60 * 1000)))}
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


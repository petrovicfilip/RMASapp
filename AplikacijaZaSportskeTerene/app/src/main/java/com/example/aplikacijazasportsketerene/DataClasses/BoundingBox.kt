package com.example.aplikacijazasportsketerene.DataClasses

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class BoundingBox(
    val minLat: Double,
    val maxLat: Double,
    val minLon: Double,
    val maxLon: Double
)

fun calculateBoundingBox(lat: Double, lon: Double, radiusInMeters: Double): BoundingBox {
    val earthRadius = 6371e3

    val latRadians = Math.toRadians(lat)

    val latDelta = radiusInMeters / earthRadius
    val lonDelta = radiusInMeters / (earthRadius * cos(latRadians))

    val minLat = lat - Math.toDegrees(latDelta)
    val maxLat = lat + Math.toDegrees(latDelta)
    val minLon = lon - Math.toDegrees(lonDelta)
    val maxLon = lon + Math.toDegrees(lonDelta)

    return BoundingBox(minLat, maxLat, minLon, maxLon)
}

fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val radius = 6371e3 // Zemljin poluprecnik u metrima
    val phi1 = Math.toRadians(lat1)
    val phi2 = Math.toRadians(lat2)
    val deltaPhi = Math.toRadians(lat2 - lat1)
    val deltaLambda = Math.toRadians(lon2 - lon1)

    val a = sin(deltaPhi / 2) * sin(deltaPhi / 2) +
            cos(phi1) * cos(phi2) *
            sin(deltaLambda / 2) * sin(deltaLambda / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return radius * c
}

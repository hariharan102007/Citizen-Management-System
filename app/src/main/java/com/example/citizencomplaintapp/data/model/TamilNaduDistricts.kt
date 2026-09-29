package com.example.citizencomplaintapp.data.model

enum class TNRegion(val title: String) {
    ALL("All 38 Districts"),
    NORTHERN("Northern TN"),
    WESTERN("Western / Kongu"),
    CENTRAL("Central & Delta"),
    SOUTHERN("Southern TN")
}

data class TNDistrict(
    val name: String,
    val lat: Double,
    val lng: Double,
    val zoneName: String,
    val popularWards: List<String>,
    val region: TNRegion = TNRegion.NORTHERN
)

object TamilNaduData {
    val ALL_38_DISTRICTS = listOf(
        // Northern Region
        TNDistrict("Chennai", 13.0827, 80.2707, "Greater Chennai Corp", listOf("Anna Salai / T. Nagar", "Marina Beach", "Velachery", "Adyar", "Guindy", "Mylapore", "Tambaram"), TNRegion.NORTHERN),
        TNDistrict("Chengalpattu", 12.6841, 79.9836, "Chengalpattu Municipality", listOf("GST Road", "Mahabalipuram Coast", "Maraimalai Nagar", "Singaperumal Koil"), TNRegion.NORTHERN),
        TNDistrict("Kanchipuram", 12.8342, 79.7036, "Kanchipuram Corp", listOf("Temple City Ward", "Gandhi Road", "Orikkai", "Kaveripakkam Rd"), TNRegion.NORTHERN),
        TNDistrict("Tiruvallur", 13.1231, 79.9120, "Tiruvallur Municipality", listOf("Veeraraghava Zone", "Avadi", "Poonamallee", "Gummidipoondi"), TNRegion.NORTHERN),
        TNDistrict("Vellore", 12.9165, 79.1325, "Vellore Corp", listOf("Fort City", "Katpadi", "Bagayam", "Sathuvachari"), TNRegion.NORTHERN),
        TNDistrict("Ranipet", 12.9272, 79.3330, "Ranipet Municipality", listOf("SIPCOT Zone", "Arcot", "Walajapet", "Arakkonam"), TNRegion.NORTHERN),
        TNDistrict("Tirupathur", 12.4996, 78.5739, "Tirupathur Municipality", listOf("Yelagiri Hills Zone", "Vaniyambadi", "Ambur Leather Belt", "Town Hall"), TNRegion.NORTHERN),
        TNDistrict("Tiruvannamalai", 12.2253, 79.0747, "Tiruvannamalai Municipality", listOf("Girivalam Path", "Arunachaleswarar Temple", "Polur Rd", "Chengam Rd"), TNRegion.NORTHERN),
        TNDistrict("Viluppuram", 11.9401, 79.4861, "Viluppuram Municipality", listOf("Old Bus Stand", "Tindivanam", "Gingee Fort Zone", "East Coast Link"), TNRegion.NORTHERN),
        TNDistrict("Cuddalore", 11.7480, 79.7714, "Cuddalore Municipality", listOf("Silver Beach Zone", "Manjakuppam", "OT Cuddalore", "Port Area"), TNRegion.NORTHERN),
        TNDistrict("Kallakurichi", 11.7384, 78.9639, "Kallakurichi Municipality", listOf("Salem Main Rd", "Sankarapuram", "Chinnasalem", "Ulundurpet"), TNRegion.NORTHERN),

        // Western / Kongu Region
        TNDistrict("Coimbatore", 11.0168, 76.9558, "Coimbatore City Corp", listOf("Gandhipuram", "RS Puram", "Peelamedu", "Ukkadam", "Saravanampatti", "Singanallur"), TNRegion.WESTERN),
        TNDistrict("Tiruppur", 11.1085, 77.3411, "Tiruppur Corp", listOf("Avinashi Road", "Old Bus Stand", "Palladam Road", "Velliyankadu"), TNRegion.WESTERN),
        TNDistrict("Erode", 11.3410, 77.7172, "Erode Corp", listOf("Perundurai Road", "Brough Road", "Surampatti", "Veerappanchatram"), TNRegion.WESTERN),
        TNDistrict("Salem", 11.6643, 78.1460, "Salem City Corp", listOf("New Bus Stand", "Hasthampatti", "Fairlands", "Suramangalam", "Ammapet"), TNRegion.WESTERN),
        TNDistrict("Namakkal", 11.2189, 78.1674, "Namakkal Municipality", listOf("Anjaneyar Temple Rd", "Mohanur Rd", "Tiruchengode Rd", "Salem Bypass"), TNRegion.WESTERN),
        TNDistrict("Karur", 10.9601, 78.0766, "Karur Corp", listOf("Textile Zone", "Thanthonimalai", "Jawahar Bazaar", "Gandhigramam"), TNRegion.WESTERN),
        TNDistrict("Dharmapuri", 12.1211, 78.1582, "Dharmapuri Municipality", listOf("Collectorate Zone", "Pennagaram Rd", "Railway Station Road", "Four Roads"), TNRegion.WESTERN),
        TNDistrict("Krishnagiri", 12.5186, 78.2137, "Krishnagiri Municipality", listOf("Hosur Industrial Belt", "Rayakottai Rd", "Old Pet", "Londonpet"), TNRegion.WESTERN),
        TNDistrict("Nilgiris", 11.4102, 76.6950, "Udhagamandalam (Ooty)", listOf("Charring Cross", "Coonoor", "Kotagiri", "Botanical Garden Zone"), TNRegion.WESTERN),

        // Central & Cauvery Delta Region
        TNDistrict("Tiruchirappalli", 10.7905, 78.7047, "Tiruchirappalli Corp", listOf("Thillai Nagar", "Srirangam", "Central Bus Stand", "Cantonment", "K.K. Nagar"), TNRegion.CENTRAL),
        TNDistrict("Thanjavur", 10.7870, 79.1378, "Thanjavur Corp", listOf("Big Temple Road", "Medical College Rd", "Old Bus Stand", "New Housing Unit"), TNRegion.CENTRAL),
        TNDistrict("Tiruvarur", 10.7725, 79.6365, "Tiruvarur Municipality", listOf("Thyagaraja Temple Zone", "Kudavasal", "Mannargudi", "South Street"), TNRegion.CENTRAL),
        TNDistrict("Nagapattinam", 10.7672, 79.8449, "Nagapattinam Municipality", listOf("Velankanni Coast", "Port Area", "Public Office Rd", "Nagore"), TNRegion.CENTRAL),
        TNDistrict("Mayiladuthurai", 11.1075, 79.6524, "Mayiladuthurai Municipality", listOf("Mayuranathar Zone", "Kaveri River Bank", "Poompuhar", "Sirkazhi"), TNRegion.CENTRAL),
        TNDistrict("Pudukkottai", 10.3797, 78.8208, "Pudukkottai Corp", listOf("Palace Road", "Santhaiyapettai", "Machuvadi", "Alangudi"), TNRegion.CENTRAL),
        TNDistrict("Ariyalur", 11.1401, 79.0786, "Ariyalur Municipality", listOf("Cement City Zone", "Jayankondam", "Sendurai", "Market Street"), TNRegion.CENTRAL),
        TNDistrict("Perambalur", 11.2342, 78.8820, "Perambalur Municipality", listOf("Bypass Road", "Veppanthattai", "Alathur", "Elambalur"), TNRegion.CENTRAL),

        // Southern Region
        TNDistrict("Madurai", 9.9252, 78.1198, "Madurai City Corp", listOf("Meenakshi Amman Temple Zone", "Mattuthavani", "Anna Nagar", "Simmakkal", "Goripalayam"), TNRegion.SOUTHERN),
        TNDistrict("Dindigul", 10.3673, 77.9803, "Dindigul Corp", listOf("Rock Fort Zone", "Round Road", "Palani Road", "Nagal Nagar"), TNRegion.SOUTHERN),
        TNDistrict("Theni", 10.0104, 77.4768, "Theni Allinagaram", listOf("Bodinayakanur Rd", "Periyakulam", "Cumbum", "Subban Street"), TNRegion.SOUTHERN),
        TNDistrict("Virudhunagar", 9.5680, 77.9624, "Virudhunagar Municipality", listOf("Sivakasi Fireworks Belt", "Aruppukkottai", "Srivilliputhur", "Madurai Rd"), TNRegion.SOUTHERN),
        TNDistrict("Ramanathapuram", 9.3639, 78.8395, "Ramanathapuram Municipality", listOf("Rameswaram Island", "Kenikarai", "Paramakudi", "Bazaar"), TNRegion.SOUTHERN),
        TNDistrict("Sivaganga", 9.8433, 78.4809, "Sivaganga Municipality", listOf("Karaikudi Chettinad", "Madurai Rd", "Manamadurai", "College Rd"), TNRegion.SOUTHERN),
        TNDistrict("Tirunelveli", 8.7139, 77.7567, "Tirunelveli Corp", listOf("Palayamkottai", "Tirunelveli Town", "Vannarpettai", "Junction"), TNRegion.SOUTHERN),
        TNDistrict("Tenkasi", 8.9594, 77.3150, "Tenkasi Municipality", listOf("Courtallam Falls Zone", "Sankarankovil", "Kasi Viswanathar Rd", "Kadayanallur"), TNRegion.SOUTHERN),
        TNDistrict("Thoothukudi", 8.7642, 78.1348, "Thoothukudi Corp", listOf("Pearl City Port", "Millerpuram", "Bryant Nagar", "V.O.C. Market"), TNRegion.SOUTHERN),
        TNDistrict("Kanyakumari", 8.1833, 77.4119, "Nagercoil Corp", listOf("Cape Comorin", "Nagercoil Central", "Vadasery", "Kottar"), TNRegion.SOUTHERN)
    )

    fun findDistrict(name: String): TNDistrict {
        return ALL_38_DISTRICTS.firstOrNull { it.name.equals(name, ignoreCase = true) }
            ?: ALL_38_DISTRICTS[0] // Default Chennai
    }

    fun findNearestDistrict(lat: Double, lng: Double): TNDistrict {
        var minDistance = Double.MAX_VALUE
        var nearest = ALL_38_DISTRICTS[0]
        for (d in ALL_38_DISTRICTS) {
            val dist = Math.hypot(lat - d.lat, lng - d.lng)
            if (dist < minDistance) {
                minDistance = dist
                nearest = d
            }
        }
        return nearest
    }
}

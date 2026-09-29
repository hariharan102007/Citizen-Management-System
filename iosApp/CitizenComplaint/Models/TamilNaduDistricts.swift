import Foundation
import CoreLocation

public enum TNRegion: String, CaseIterable, Identifiable, Codable {
    case all = "All 38 Districts"
    case northern = "Northern TN"
    case western = "Western / Kongu"
    case central = "Central & Delta"
    case southern = "Southern TN"
    
    public var id: String { rawValue }
}

public struct TNDistrict: Identifiable, Hashable, Codable {
    public var id: String { name }
    public let name: String
    public let latitude: Double
    public let longitude: Double
    public let zoneName: String
    public let popularWards: [String]
    public let region: TNRegion
    
    public var coordinate: CLLocationCoordinate2D {
        CLLocationCoordinate2D(latitude: latitude, longitude: longitude)
    }
}

public struct TamilNaduData {
    public static let all38Districts: [TNDistrict] = [
        // Northern Region (11 districts)
        TNDistrict(name: "Chennai", latitude: 13.0827, longitude: 80.2707, zoneName: "Greater Chennai Corp", popularWards: ["Anna Salai / T. Nagar", "Marina Beach", "Velachery", "Adyar", "Guindy", "Mylapore", "Tambaram"], region: .northern),
        TNDistrict(name: "Chengalpattu", latitude: 12.6841, longitude: 79.9836, zoneName: "Chengalpattu Municipality", popularWards: ["GST Road", "Mahabalipuram Coast", "Maraimalai Nagar", "Singaperumal Koil"], region: .northern),
        TNDistrict(name: "Kanchipuram", latitude: 12.8342, longitude: 79.7036, zoneName: "Kanchipuram Corp", popularWards: ["Temple City Ward", "Gandhi Road", "Orikkai", "Kaveripakkam Rd"], region: .northern),
        TNDistrict(name: "Tiruvallur", latitude: 13.1231, longitude: 79.9120, zoneName: "Tiruvallur Municipality", popularWards: ["Veeraraghava Zone", "Avadi", "Poonamallee", "Gummidipoondi"], region: .northern),
        TNDistrict(name: "Vellore", latitude: 12.9165, longitude: 79.1325, zoneName: "Vellore Corp", popularWards: ["Fort City", "Katpadi", "Bagayam", "Sathuvachari"], region: .northern),
        TNDistrict(name: "Ranipet", latitude: 12.9272, longitude: 79.3330, zoneName: "Ranipet Municipality", popularWards: ["SIPCOT Zone", "Arcot", "Walajapet", "Arakkonam"], region: .northern),
        TNDistrict(name: "Tirupathur", latitude: 12.4996, longitude: 78.5739, zoneName: "Tirupathur Municipality", popularWards: ["Yelagiri Hills Zone", "Vaniyambadi", "Ambur Leather Belt", "Town Hall"], region: .northern),
        TNDistrict(name: "Tiruvannamalai", latitude: 12.2253, longitude: 79.0747, zoneName: "Tiruvannamalai Municipality", popularWards: ["Girivalam Path", "Arunachaleswarar Temple", "Polur Rd", "Chengam Rd"], region: .northern),
        TNDistrict(name: "Viluppuram", latitude: 11.9401, longitude: 79.4861, zoneName: "Viluppuram Municipality", popularWards: ["Old Bus Stand", "Tindivanam", "Gingee Fort Zone", "East Coast Link"], region: .northern),
        TNDistrict(name: "Cuddalore", latitude: 11.7480, longitude: 79.7714, zoneName: "Cuddalore Municipality", popularWards: ["Silver Beach Zone", "Manjakuppam", "OT Cuddalore", "Port Area"], region: .northern),
        TNDistrict(name: "Kallakurichi", latitude: 11.7384, longitude: 78.9639, zoneName: "Kallakurichi Municipality", popularWards: ["Salem Main Rd", "Sankarapuram", "Chinnasalem", "Ulundurpet"], region: .northern),

        // Western / Kongu Region (9 districts)
        TNDistrict(name: "Coimbatore", latitude: 11.0168, longitude: 76.9558, zoneName: "Coimbatore City Corp", popularWards: ["Gandhipuram", "RS Puram", "Peelamedu", "Ukkadam", "Saravanampatti", "Singanallur"], region: .western),
        TNDistrict(name: "Tiruppur", latitude: 11.1085, longitude: 77.3411, zoneName: "Tiruppur Corp", popularWards: ["Avinashi Road", "Old Bus Stand", "Palladam Road", "Velliyankadu"], region: .western),
        TNDistrict(name: "Erode", latitude: 11.3410, longitude: 77.7172, zoneName: "Erode Corp", popularWards: ["Perundurai Road", "Brough Road", "Surampatti", "Veerappanchatram"], region: .western),
        TNDistrict(name: "Salem", latitude: 11.6643, longitude: 78.1460, zoneName: "Salem City Corp", popularWards: ["New Bus Stand", "Hasthampatti", "Fairlands", "Suramangalam", "Ammapet"], region: .western),
        TNDistrict(name: "Namakkal", latitude: 11.2189, longitude: 78.1674, zoneName: "Namakkal Municipality", popularWards: ["Anjaneyar Temple Rd", "Mohanur Rd", "Tiruchengode Rd", "Salem Bypass"], region: .western),
        TNDistrict(name: "Karur", latitude: 10.9601, longitude: 78.0766, zoneName: "Karur Corp", popularWards: ["Textile Zone", "Thanthonimalai", "Jawahar Bazaar", "Gandhigramam"], region: .western),
        TNDistrict(name: "Dharmapuri", latitude: 12.1211, longitude: 78.1582, zoneName: "Dharmapuri Municipality", popularWards: ["Collectorate Zone", "Pennagaram Rd", "Railway Station Road", "Four Roads"], region: .western),
        TNDistrict(name: "Krishnagiri", latitude: 12.5186, longitude: 78.2137, zoneName: "Krishnagiri Municipality", popularWards: ["Hosur Industrial Belt", "Rayakottai Rd", "Old Pet", "Londonpet"], region: .western),
        TNDistrict(name: "Nilgiris", latitude: 11.4102, longitude: 76.6950, zoneName: "Udhagamandalam (Ooty)", popularWards: ["Charring Cross", "Coonoor", "Kotagiri", "Botanical Garden Zone"], region: .western),

        // Central & Cauvery Delta Region (8 districts)
        TNDistrict(name: "Tiruchirappalli", latitude: 10.7905, longitude: 78.7047, zoneName: "Tiruchirappalli Corp", popularWards: ["Thillai Nagar", "Srirangam", "Central Bus Stand", "Cantonment", "K.K. Nagar"], region: .central),
        TNDistrict(name: "Thanjavur", latitude: 10.7870, longitude: 79.1378, zoneName: "Thanjavur Corp", popularWards: ["Big Temple Road", "Medical College Rd", "Old Bus Stand", "New Housing Unit"], region: .central),
        TNDistrict(name: "Tiruvarur", latitude: 10.7725, longitude: 79.6365, zoneName: "Tiruvarur Municipality", popularWards: ["Thyagaraja Temple Zone", "Kudavasal", "Mannargudi", "South Street"], region: .central),
        TNDistrict(name: "Nagapattinam", latitude: 10.7672, longitude: 79.8449, zoneName: "Nagapattinam Municipality", popularWards: ["Velankanni Coast", "Port Area", "Public Office Rd", "Nagore"], region: .central),
        TNDistrict(name: "Mayiladuthurai", latitude: 11.1075, longitude: 79.6524, zoneName: "Mayiladuthurai Municipality", popularWards: ["Mayuranathar Zone", "Kaveri River Bank", "Poompuhar", "Sirkazhi"], region: .central),
        TNDistrict(name: "Pudukkottai", latitude: 10.3797, longitude: 78.8208, zoneName: "Pudukkottai Corp", popularWards: ["Palace Road", "Santhaiyapettai", "Machuvadi", "Alangudi"], region: .central),
        TNDistrict(name: "Ariyalur", latitude: 11.1401, longitude: 79.0786, zoneName: "Ariyalur Municipality", popularWards: ["Cement City Zone", "Jayankondam", "Sendurai", "Market Street"], region: .central),
        TNDistrict(name: "Perambalur", latitude: 11.2342, longitude: 78.8820, zoneName: "Perambalur Municipality", popularWards: ["Bypass Road", "Veppanthattai", "Alathur", "Elambalur"], region: .central),

        // Southern Region (10 districts)
        TNDistrict(name: "Madurai", latitude: 9.9252, longitude: 78.1198, zoneName: "Madurai City Corp", popularWards: ["Meenakshi Amman Temple Zone", "Mattuthavani", "Anna Nagar", "Simmakkal", "Goripalayam"], region: .southern),
        TNDistrict(name: "Dindigul", latitude: 10.3673, longitude: 77.9803, zoneName: "Dindigul Corp", popularWards: ["Rock Fort Zone", "Round Road", "Palani Road", "Nagal Nagar"], region: .southern),
        TNDistrict(name: "Theni", latitude: 10.0104, longitude: 77.4768, zoneName: "Theni Allinagaram", popularWards: ["Bodinayakanur Rd", "Periyakulam", "Cumbum", "Subban Street"], region: .southern),
        TNDistrict(name: "Virudhunagar", latitude: 9.5680, longitude: 77.9624, zoneName: "Virudhunagar Municipality", popularWards: ["Sivakasi Fireworks Belt", "Aruppukkottai", "Srivilliputhur", "Madurai Rd"], region: .southern),
        TNDistrict(name: "Sivaganga", latitude: 9.8433, longitude: 78.4809, zoneName: "Sivaganga Municipality", popularWards: ["Karaikudi Chettinad Belt", "Devakottai", "Manamadurai", "Palace Zone"], region: .southern),
        TNDistrict(name: "Ramanathapuram", latitude: 9.3639, longitude: 78.8395, zoneName: "Ramanathapuram Municipality", popularWards: ["Rameswaram Island Rd", "Mandapam Coast", "Paramakudi", "Kilakarai"], region: .southern),
        TNDistrict(name: "Thoothukudi", latitude: 8.7642, longitude: 78.1348, zoneName: "Thoothukudi City Corp", popularWards: ["Pearl City Port Zone", "Palayamkottai Rd", "Cruz Fernandez Ward", "Millerpuram"], region: .southern),
        TNDistrict(name: "Tirunelveli", latitude: 8.7139, longitude: 77.7567, zoneName: "Tirunelveli City Corp", popularWards: ["Nellaiappar Temple Zone", "Palayamkottai", "Vannarpettai", "Town Hall"], region: .southern),
        TNDistrict(name: "Tenkasi", latitude: 8.9594, longitude: 77.3150, zoneName: "Tenkasi Municipality", popularWards: ["Courtallam Falls Ward", "Kasi Viswanathar Temple", "Sankarankovil", "Kadayanallur"], region: .southern),
        TNDistrict(name: "Kanniyakumari", latitude: 8.0883, longitude: 77.5385, zoneName: "Nagercoil City Corp", popularWards: ["Cape Comorin Point", "Nagercoil Town", "Vadasery", "Colachel Coast"], region: .southern)
    ]
    
    public static func nearestDistrict(to coordinate: CLLocationCoordinate2D) -> TNDistrict {
        var closest = all38Districts[0]
        var minDistance = Double.infinity
        
        for district in all38Districts {
            let latDiff = district.latitude - coordinate.latitude
            let lngDiff = district.longitude - coordinate.longitude
            let dist = (latDiff * latDiff) + (lngDiff * lngDiff)
            if dist < minDistance {
                minDistance = dist
                closest = district
            }
        }
        return closest
    }
}

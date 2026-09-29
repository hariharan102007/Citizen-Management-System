import Foundation
import CoreLocation

public enum ComplaintStatus: String, CaseIterable, Identifiable, Codable {
    case submitted = "Submitted"
    case assigned = "Assigned"
    case officerTraveling = "Officer Traveling"
    case workStarted = "Work Started"
    case inProgress = "In Progress"
    case resolved = "Resolved"
    case closed = "Closed"
    
    public var id: String { rawValue }
    
    public var iconName: String {
        switch self {
        case .submitted: return "doc.text"
        case .assigned: return "person.badge.shield.checkmark"
        case .officerTraveling: return "figure.walk"
        case .workStarted: return "hammer.fill"
        case .inProgress: return "clock.arrow.circlepath"
        case .resolved: return "checkmark.seal.fill"
        case .closed: return "lock.fill"
        }
    }
}

public enum Priority: String, CaseIterable, Identifiable, Codable {
    case low = "Low"
    case medium = "Medium"
    case high = "High"
    case emergency = "Emergency"
    
    public var id: String { rawValue }
}

public struct StatusHistoryItem: Identifiable, Codable {
    public var id = UUID()
    public let status: ComplaintStatus
    public let date: Date
    public let notes: String
}

public struct Remark: Identifiable, Codable {
    public var id = UUID()
    public let author: String
    public let message: String
    public let timeAgo: String
    public let isOfficial: BooleanLiteralType
}

public struct Complaint: Identifiable, Codable {
    public var id: String
    public var title: String
    public var description: String
    public var categoryName: String
    public var department: String
    public var location: String
    public var status: ComplaintStatus
    public var priority: Priority
    public var createdAt: Date
    public var supportCount: Int
    public var officerName: String?
    public var latitude: Double
    public var longitude: Double
    public var isAnonymous: Bool
    public var statusHistory: [StatusHistoryItem]
    
    public var coordinate: CLLocationCoordinate2D {
        CLLocationCoordinate2D(latitude: latitude, longitude: longitude)
    }
    
    public static let sampleComplaints: [Complaint] = [
        Complaint(
            id: "CMP-TN-2026-001",
            title: "Road Pothole on Mount Road",
            description: "Deep pothole creating severe traffic hazard near Guindy intersection during peak hours.",
            categoryName: "Roads & Potholes",
            department: "Greater Chennai Corporation (GCC)",
            location: "Anna Salai, Near Mount Road / Guindy, Chennai",
            status: .workStarted,
            priority: .high,
            createdAt: Date(timeIntervalSinceNow: -86400 * 3),
            supportCount: 42,
            officerName: "Eng. R. Selvakumar",
            latitude: 13.0100,
            longitude: 80.2100,
            isAnonymous: false,
            statusHistory: [
                StatusHistoryItem(status: .submitted, date: Date(timeIntervalSinceNow: -86400 * 3), notes: "Complaint registered by citizen"),
                StatusHistoryItem(status: .assigned, date: Date(timeIntervalSinceNow: -86400 * 2), notes: "Assigned to GCC Road Maintenance Division"),
                StatusHistoryItem(status: .workStarted, date: Date(timeIntervalSinceNow: -86400 * 1), notes: "Bitumen patch crew deployed")
            ]
        ),
        Complaint(
            id: "CMP-TN-2026-002",
            title: "Commercial Gas Leakage Alert",
            description: "Strong smell of LPG gas near commercial complex basement on Cross Cut Road.",
            categoryName: "Public Safety",
            department: "Fire & Rescue Services / Emergency",
            location: "Cross Cut Rd, Gandhipuram, Coimbatore",
            status: .officerTraveling,
            priority: .emergency,
            createdAt: Date(timeIntervalSinceNow: -7200),
            supportCount: 89,
            officerName: "Inspector K. Murugan",
            latitude: 11.0168,
            longitude: 76.9558,
            isAnonymous: false,
            statusHistory: [
                StatusHistoryItem(status: .submitted, date: Date(timeIntervalSinceNow: -7200), notes: "Urgent safety alert filed"),
                StatusHistoryItem(status: .officerTraveling, date: Date(timeIntervalSinceNow: -3600), notes: "Coimbatore Station 4 crew en route")
            ]
        ),
        Complaint(
            id: "CMP-TN-2026-003",
            title: "Streetlight outage near Temple Ward",
            description: "Entire street dark on North Chitrai Street, causing safety issues for evening devotees.",
            categoryName: "Streetlights",
            department: "TANGEDCO / Madurai Corp",
            location: "North Chitrai St, Temple Ward, Madurai",
            status: .resolved,
            priority: .medium,
            createdAt: Date(timeIntervalSinceNow: -86400 * 5),
            supportCount: 19,
            officerName: "Lineman S. Anbarasu",
            latitude: 9.9252,
            longitude: 78.1198,
            isAnonymous: false,
            statusHistory: [
                StatusHistoryItem(status: .submitted, date: Date(timeIntervalSinceNow: -86400 * 5), notes: "Complaint lodged"),
                StatusHistoryItem(status: .resolved, date: Date(timeIntervalSinceNow: -86400 * 1), notes: "LED junction fixture replaced")
            ]
        ),
        Complaint(
            id: "CMP-TN-2026-004",
            title: "Drainage Overflow near Big Temple Road",
            description: "Sewage water leaking across pedestrian walkway near entrance.",
            categoryName: "Sanitation & Drainage",
            department: "Thanjavur Municipal Corporation",
            location: "Big Temple Road, Thanjavur",
            status: .assigned,
            priority: .high,
            createdAt: Date(timeIntervalSinceNow: -86400 * 2),
            supportCount: 31,
            officerName: "Sanitary Officer V. Karthik",
            latitude: 10.7870,
            longitude: 79.1378,
            isAnonymous: false,
            statusHistory: [
                StatusHistoryItem(status: .submitted, date: Date(timeIntervalSinceNow: -86400 * 2), notes: "Report logged"),
                StatusHistoryItem(status: .assigned, date: Date(timeIntervalSinceNow: -86400 * 1), notes: "Assigned to desilting team")
            ]
        )
    ]
}

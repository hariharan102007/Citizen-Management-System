import Foundation
import Combine

public class ComplaintViewModel: ObservableObject {
    @Published public var complaints: [Complaint] = Complaint.sampleComplaints
    @Published public var selectedFilterStatus: ComplaintStatus? = nil
    @Published public var selectedFilterDistrict: String = "All"
    @Published public var searchText: String = ""
    
    public let departments = [
        "Roads & Potholes",
        "Water Supply & Leakages",
        "Electricity & Streetlights",
        "Sanitation & Solid Waste",
        "Public Safety & Fire",
        "Town Planning & Encroachment",
        "Public Health & Mosquito Control",
        "Revenue & Civic Grievance"
    ]
    
    public var filteredComplaints: [Complaint] {
        complaints.filter { complaint in
            let matchesSearch = searchText.isEmpty ||
                complaint.title.localizedCaseInsensitiveContains(searchText) ||
                complaint.description.localizedCaseInsensitiveContains(searchText) ||
                complaint.location.localizedCaseInsensitiveContains(searchText) ||
                complaint.categoryName.localizedCaseInsensitiveContains(searchText)
            
            let matchesStatus = selectedFilterStatus == nil || complaint.status == selectedFilterStatus
            let matchesDistrict = selectedFilterDistrict == "All" || complaint.location.localizedCaseInsensitiveContains(selectedFilterDistrict)
            
            return matchesSearch && matchesStatus && matchesDistrict
        }
    }
    
    public var totalCount: Int { complaints.count }
    public var resolvedCount: Int { complaints.filter { $0.status == .resolved || $0.status == .closed }.count }
    public var pendingCount: Int { complaints.filter { $0.status != .resolved && $0.status != .closed }.count }
    
    public func addComplaint(
        title: String,
        description: String,
        department: String,
        location: String,
        priority: Priority,
        latitude: Double,
        longitude: Double,
        isAnonymous: Bool
    ) {
        let newComplaint = Complaint(
            id: "CMP-TN-2026-\(Int.random(in: 1000...9999))",
            title: title,
            description: description,
            categoryName: department,
            department: "Tamil Nadu Civic Administration",
            location: location,
            status: .submitted,
            priority: priority,
            createdAt: Date(),
            supportCount: 1,
            officerName: nil,
            latitude: latitude,
            longitude: longitude,
            isAnonymous: isAnonymous,
            statusHistory: [
                StatusHistoryItem(status: .submitted, date: Date(), notes: "Submitted via iPhone Citizen App")
            ]
        )
        complaints.insert(newComplaint, at: 0)
    }
    
    public func upvote(complaintId: String) {
        if let index = complaints.firstIndex(where: { $0.id == complaintId }) {
            complaints[index].supportCount += 1
        }
    }
}

import SwiftUI

public struct ComplaintsListView: View {
    @ObservedObject var viewModel: ComplaintViewModel
    @State private var selectedStatus: ComplaintStatus? = nil
    
    public var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                // Top Status Filter Pills
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 8) {
                        Button(action: {
                            viewModel.selectedFilterStatus = nil
                        }) {
                            Text("All (\(viewModel.totalCount))")
                                .font(.caption)
                                .fontWeight(viewModel.selectedFilterStatus == nil ? .bold : .medium)
                                .padding(.horizontal, 12)
                                .padding(.vertical, 6)
                                .background(viewModel.selectedFilterStatus == nil ? Color.blue : Color(UIColor.secondarySystemBackground))
                                .foregroundColor(viewModel.selectedFilterStatus == nil ? .white : .primary)
                                .cornerRadius(16)
                        }
                        
                        ForEach(ComplaintStatus.allCases) { status in
                            Button(action: {
                                viewModel.selectedFilterStatus = status
                            }) {
                                HStack(spacing: 4) {
                                    Image(systemName: status.iconName)
                                    Text(status.rawValue)
                                }
                                .font(.caption)
                                .fontWeight(viewModel.selectedFilterStatus == status ? .bold : .medium)
                                .padding(.horizontal, 12)
                                .padding(.vertical, 6)
                                .background(viewModel.selectedFilterStatus == status ? Color.blue : Color(UIColor.secondarySystemBackground))
                                .foregroundColor(viewModel.selectedFilterStatus == status ? .white : .primary)
                                .cornerRadius(16)
                            }
                        }
                    }
                    .padding(.horizontal, 16)
                    .padding(.vertical, 8)
                }
                .background(Color(UIColor.systemBackground))
                
                // List of Complaints
                List {
                    ForEach(viewModel.filteredComplaints) { complaint in
                        VStack(alignment: .leading, spacing: 8) {
                            HStack {
                                Text(complaint.id)
                                    .font(.caption2)
                                    .fontWeight(.bold)
                                    .foregroundColor(.secondary)
                                
                                Spacer()
                                
                                // Priority badge
                                Text(complaint.priority.rawValue)
                                    .font(.caption2)
                                    .fontWeight(.bold)
                                    .padding(.horizontal, 6)
                                    .padding(.vertical, 2)
                                    .background(priorityColor(complaint.priority).opacity(0.15))
                                    .foregroundColor(priorityColor(complaint.priority))
                                    .cornerRadius(4)
                                
                                // Status badge
                                Text(complaint.status.rawValue)
                                    .font(.caption2)
                                    .fontWeight(.bold)
                                    .padding(.horizontal, 6)
                                    .padding(.vertical, 2)
                                    .background(statusColor(complaint.status).opacity(0.15))
                                    .foregroundColor(statusColor(complaint.status))
                                    .cornerRadius(4)
                            }
                            
                            Text(complaint.title)
                                .font(.headline)
                                .foregroundColor(.primary)
                            
                            Text(complaint.description)
                                .font(.subheadline)
                                .foregroundColor(.secondary)
                                .lineLimit(2)
                            
                            HStack(spacing: 4) {
                                Image(systemName: "mappin.and.ellipse")
                                    .font(.caption)
                                    .foregroundColor(.red)
                                Text(complaint.location)
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                                    .lineLimit(1)
                            }
                            
                            HStack {
                                Text(complaint.categoryName)
                                    .font(.caption2)
                                    .padding(.horizontal, 8)
                                    .padding(.vertical, 3)
                                    .background(Color(UIColor.secondarySystemBackground))
                                    .cornerRadius(6)
                                
                                Spacer()
                                
                                Button(action: {
                                    viewModel.upvote(complaintId: complaint.id)
                                }) {
                                    HStack(spacing: 4) {
                                        Image(systemName: "hand.thumbsup.fill")
                                            .font(.caption)
                                        Text("\(complaint.supportCount) Upvotes")
                                            .font(.caption)
                                            .fontWeight(.semibold)
                                    }
                                    .foregroundColor(.blue)
                                }
                                .buttonStyle(BorderlessButtonStyle())
                            }
                        }
                        .padding(.vertical, 6)
                    }
                }
                .listStyle(PlainListStyle())
                .searchable(text: $viewModel.searchText, prompt: "Search complaints in Tamil Nadu...")
            }
            .navigationTitle("Community Complaints")
        }
    }
    
    private func statusColor(_ status: ComplaintStatus) -> Color {
        switch status {
        case .submitted: return .orange
        case .assigned: return .blue
        case .officerTraveling: return .purple
        case .workStarted: return .indigo
        case .inProgress: return .yellow
        case .resolved, .closed: return .green
        }
    }
    
    private func priorityColor(_ priority: Priority) -> Color {
        switch priority {
        case .low: return .gray
        case .medium: return .blue
        case .high: return .orange
        case .emergency: return .red
        }
    }
}

import SwiftUI

public struct TamilNaduDistrictSelectorView: View {
    @Environment(\.dismiss) private var dismiss
    @State private var searchText: String = ""
    @State private var selectedRegion: TNRegion = .all
    
    public var onSelectDistrict: (TNDistrict, String?) -> Void
    
    var filteredDistricts: [TNDistrict] {
        TamilNaduData.all38Districts.filter { district in
            let matchesRegion = selectedRegion == .all || district.region == selectedRegion
            let matchesSearch = searchText.isEmpty ||
                district.name.localizedCaseInsensitiveContains(searchText) ||
                district.zoneName.localizedCaseInsensitiveContains(searchText) ||
                district.popularWards.contains(where: { $0.localizedCaseInsensitiveContains(searchText) })
            return matchesRegion && matchesSearch
        }
    }
    
    public var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                // Region filter picker
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 8) {
                        ForEach(TNRegion.allCases) { region in
                            Button(action: {
                                selectedRegion = region
                            }) {
                                Text(region.rawValue)
                                    .font(.subheadline)
                                    .fontWeight(selectedRegion == region ? .semibold : .regular)
                                    .padding(.horizontal, 14)
                                    .padding(.vertical, 8)
                                    .background(selectedRegion == region ? Color.blue : Color(UIColor.secondarySystemBackground))
                                    .foregroundColor(selectedRegion == region ? .white : .primary)
                                    .cornerRadius(20)
                            }
                        }
                    }
                    .padding(.horizontal, 16)
                    .padding(.vertical, 10)
                }
                .background(Color(UIColor.systemBackground))
                
                // District List
                List {
                    Section(header: Text("Tamil Nadu Districts (\(filteredDistricts.count) of 38)")) {
                        ForEach(filteredDistricts) { district in
                            VStack(alignment: .leading, spacing: 8) {
                                HStack {
                                    VStack(alignment: .leading, spacing: 2) {
                                        Text(district.name)
                                            .font(.headline)
                                            .foregroundColor(.primary)
                                        Text(district.zoneName)
                                            .font(.caption)
                                            .foregroundColor(.secondary)
                                    }
                                    
                                    Spacer()
                                    
                                    Text(district.region.rawValue)
                                        .font(.caption2)
                                        .fontWeight(.medium)
                                        .padding(.horizontal, 8)
                                        .padding(.vertical, 4)
                                        .background(Color.blue.opacity(0.12))
                                        .foregroundColor(.blue)
                                        .cornerRadius(8)
                                }
                                
                                // Popular Wards chips
                                ScrollView(.horizontal, showsIndicators: false) {
                                    HStack(spacing: 6) {
                                        ForEach(district.popularWards, id: \.self) { ward in
                                            Button(action: {
                                                onSelectDistrict(district, ward)
                                                dismiss()
                                            }) {
                                                Text(ward)
                                                    .font(.caption)
                                                    .padding(.horizontal, 8)
                                                    .padding(.vertical, 4)
                                                    .background(Color(UIColor.tertiarySystemBackground))
                                                    .foregroundColor(.primary)
                                                    .overlay(
                                                        RoundedRectangle(cornerRadius: 6)
                                                            .stroke(Color.secondary.opacity(0.3), lineWidth: 0.8)
                                                    )
                                                    .cornerRadius(6)
                                            }
                                        }
                                    }
                                }
                                
                                Button(action: {
                                    onSelectDistrict(district, nil)
                                    dismiss()
                                }) {
                                    HStack {
                                        Image(systemName: "mappin.and.ellipse")
                                        Text("Select \(district.name) District Center")
                                    }
                                    .font(.footnote)
                                    .fontWeight(.medium)
                                    .foregroundColor(.blue)
                                }
                                .padding(.top, 2)
                            }
                            .padding(.vertical, 4)
                        }
                    }
                }
                .listStyle(InsetGroupedListStyle())
                .searchable(text: $searchText, prompt: "Search district or ward (e.g. Guindy, Gandhipuram)")
            }
            .navigationTitle("38 TN Districts & Wards")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancel") {
                        dismiss()
                    }
                }
            }
        }
    }
}

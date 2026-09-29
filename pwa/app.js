// Service Worker Registration
if ('serviceWorker' in navigator) {
  navigator.serviceWorker.register('./sw.js').catch(err => console.log('SW registration failed:', err));
}

// Default initial complaints in Tamil Nadu
const INITIAL_COMPLAINTS = [
  {
    id: "CMP-TN-2026-001",
    title: "Large Pothole on Mount Road",
    description: "Deep pothole causing accidents near Guindy intersection during peak traffic hours.",
    department: "Roads & Potholes",
    location: "Anna Salai, Near Mount Road / Guindy, Chennai",
    priority: "High",
    status: "Work Started",
    lat: 13.0100,
    lng: 80.2100,
    upvotes: 42,
    createdAt: "2 days ago"
  },
  {
    id: "CMP-TN-2026-002",
    title: "Commercial Gas Leakage Alert",
    description: "Strong smell of LPG gas near basement complex on Cross Cut Road.",
    department: "Public Safety & Emergency",
    location: "Cross Cut Rd, Gandhipuram, Coimbatore",
    priority: "Emergency",
    status: "Assigned",
    lat: 11.0168,
    lng: 76.9558,
    upvotes: 89,
    createdAt: "1 hour ago"
  },
  {
    id: "CMP-TN-2026-003",
    title: "Streetlight outage near Temple Ward",
    description: "Dark street causing safety concerns for evening temple devotees.",
    department: "Electricity & Streetlights",
    location: "North Chitrai St, Temple Ward, Madurai",
    priority: "Medium",
    status: "Resolved",
    lat: 9.9252,
    lng: 78.1198,
    upvotes: 19,
    createdAt: "3 days ago"
  },
  {
    id: "CMP-TN-2026-004",
    title: "Drainage Overflow near Big Temple",
    description: "Sewage water leaking across walkway near entrance.",
    department: "Sanitation & Drainage",
    location: "Big Temple Road, Thanjavur",
    priority: "High",
    status: "Assigned",
    lat: 10.7870,
    lng: 79.1378,
    upvotes: 31,
    createdAt: "Yesterday"
  }
];

// App State
let complaints = JSON.parse(localStorage.getItem('tn_complaints')) || INITIAL_COMPLAINTS;
let currentPriority = 'Medium';
let currentSelectedCoordinate = { lat: 13.0827, lng: 80.2707 };
let currentSelectedDistrict = ALL_38_DISTRICTS[0];
let currentSelectedAddress = "Anna Salai, Chennai, Tamil Nadu";
let selectedRegionFilter = "All";
let selectedDistrictFilter = "All";

// Maps
let mainMap = null;
let pickerMap = null;
let pickerMarker = null;

// Initialize app on DOM ready
document.addEventListener('DOMContentLoaded', () => {
  renderComplaintsList();
  renderDistrictsList();
});

// Tab Switching
function switchTab(tabId) {
  const tabs = ['complaints', 'map', 'report', 'guide'];
  tabs.forEach(t => {
    const el = document.getElementById(`tab-${t}`);
    const btn = document.getElementById(`tab-btn-${t}`);
    if (t === tabId) {
      el.classList.remove('hidden');
      btn.classList.add('text-blue-600');
      btn.classList.remove('text-slate-400');
    } else {
      el.classList.add('hidden');
      btn.classList.remove('text-blue-600');
      btn.classList.add('text-slate-400');
    }
  });

  if (tabId === 'map') {
    setTimeout(initMainMap, 100);
  }
}

// Render Complaints List
function renderComplaintsList() {
  const container = document.getElementById('complaints-list');
  const countBadge = document.getElementById('complaints-count-badge');
  
  const filtered = complaints.filter(c => {
    if (selectedDistrictFilter === 'All') return true;
    return c.location.toLowerCase().includes(selectedDistrictFilter.toLowerCase());
  });

  countBadge.innerText = `${filtered.length} Grievances`;

  if (filtered.length === 0) {
    container.innerHTML = `
      <div class="text-center py-12 text-slate-400">
        <p class="text-3xl mb-2">📭</p>
        <p class="font-bold text-sm">No grievances found in this district.</p>
        <p class="text-xs">Tap "Report" below to log a civic issue.</p>
      </div>
    `;
    return;
  }

  container.innerHTML = filtered.map(c => `
    <div class="bg-white rounded-2xl p-4 shadow-sm border border-slate-200 space-y-2">
      <div class="flex items-center justify-between text-[11px]">
        <span class="font-mono font-bold text-slate-400">${c.id}</span>
        <div class="flex space-x-1.5">
          <span class="px-2 py-0.5 rounded-md font-bold ${getPriorityBadgeClass(c.priority)}">${c.priority}</span>
          <span class="px-2 py-0.5 rounded-md font-bold ${getStatusBadgeClass(c.status)}">${c.status}</span>
        </div>
      </div>
      <h3 class="font-bold text-slate-800 text-sm">${c.title}</h3>
      <p class="text-xs text-slate-600 line-clamp-2">${c.description}</p>
      <div class="flex items-center text-xs text-slate-500 space-x-1 pt-1">
        <span class="text-red-500">📍</span>
        <span class="truncate">${c.location}</span>
      </div>
      <div class="flex items-center justify-between pt-2 border-t border-slate-100 text-xs">
        <span class="bg-slate-100 text-slate-600 px-2 py-0.5 rounded font-medium">${c.department}</span>
        <button onclick="upvoteComplaint('${c.id}')" class="text-blue-600 font-bold flex items-center space-x-1 active:scale-95 transition">
          <span>👍</span>
          <span>${c.upvotes} Upvotes</span>
        </button>
      </div>
    </div>
  `).join('');
}

function getPriorityBadgeClass(priority) {
  switch (priority) {
    case 'Emergency': return 'bg-red-100 text-red-700';
    case 'High': return 'bg-orange-100 text-orange-700';
    case 'Medium': return 'bg-blue-100 text-blue-700';
    default: return 'bg-slate-100 text-slate-700';
  }
}

function getStatusBadgeClass(status) {
  switch (status) {
    case 'Resolved': return 'bg-emerald-100 text-emerald-700';
    case 'Work Started': return 'bg-indigo-100 text-indigo-700';
    case 'Assigned': return 'bg-blue-100 text-blue-700';
    default: return 'bg-amber-100 text-amber-700';
  }
}

function filterByDistrict(district) {
  selectedDistrictFilter = district;
  document.querySelectorAll('.district-filter-pill').forEach(btn => {
    if (btn.innerText.includes(district) || (district === 'All' && btn.innerText === 'All TN') || (district === 'Tiruchirappalli' && btn.innerText === 'Trichy')) {
      btn.classList.add('bg-blue-600', 'text-white');
      btn.classList.remove('bg-slate-200', 'text-slate-700');
    } else {
      btn.classList.remove('bg-blue-600', 'text-white');
      btn.classList.add('bg-slate-200', 'text-slate-700');
    }
  });
  renderComplaintsList();
}

function upvoteComplaint(id) {
  const c = complaints.find(item => item.id === id);
  if (c) {
    c.upvotes += 1;
    localStorage.setItem('tn_complaints', JSON.stringify(complaints));
    renderComplaintsList();
  }
}

// Priority Selector
function setPriority(level) {
  currentPriority = level;
  document.querySelectorAll('.priority-btn').forEach(btn => {
    if (btn.innerText.includes(level) || (level === 'Emergency' && btn.innerText === 'Alert')) {
      btn.className = 'priority-btn active px-2 py-1.5 text-xs font-bold rounded-lg bg-blue-600 text-white';
    } else {
      btn.className = 'priority-btn px-2 py-1.5 text-xs font-bold rounded-lg border border-slate-200 bg-slate-100 text-slate-700';
    }
  });
}

// Live GPS Detection (iPhone Native Web Geolocation API)
function detectLiveGPS() {
  const icon = document.getElementById('gps-icon');
  const spinner = document.getElementById('gps-spinner');
  
  if (!navigator.geolocation) {
    alert("Geolocation is not supported by your browser.");
    return;
  }

  icon.classList.add('hidden');
  spinner.classList.remove('hidden');

  navigator.geolocation.getCurrentPosition(
    position => {
      icon.classList.remove('hidden');
      spinner.classList.add('hidden');

      const lat = position.coords.latitude;
      const lng = position.coords.longitude;
      applyLocationCoordinates(lat, lng, true);
    },
    error => {
      icon.classList.remove('hidden');
      spinner.classList.add('hidden');
      alert("GPS detection failed: " + error.message + ". Please select on map or pick from 38 districts.");
      // Fallback to Chennai
      applyLocationCoordinates(13.0827, 80.2707, false);
    },
    { enableHighAccuracy: true, timeout: 10000 }
  );
}

function applyLocationCoordinates(lat, lng, isLive) {
  currentSelectedCoordinate = { lat, lng };
  const nearest = findNearestTNDistrict(lat, lng);
  currentSelectedDistrict = nearest;

  const banner = document.getElementById('gps-banner');
  const coordsText = document.getElementById('gps-coords-text');
  const verifiedTitle = document.getElementById('gps-verified-title');
  const locInput = document.getElementById('input-location');

  banner.classList.remove('hidden');
  coordsText.innerText = `Lat: ${lat.toFixed(5)}, Lng: ${lng.toFixed(5)}`;
  verifiedTitle.innerText = `${nearest.name} District (${nearest.zone})`;

  const ward = nearest.wards[0] || "Main Road";
  currentSelectedAddress = `${ward}, ${nearest.name}, Tamil Nadu`;
  locInput.value = currentSelectedAddress;
}

// Map Picker Modal
function openMapPickerModal() {
  document.getElementById('modal-map-picker').classList.remove('hidden');
  setTimeout(() => {
    initPickerMap();
  }, 100);
}

function closeMapPickerModal() {
  document.getElementById('modal-map-picker').classList.add('hidden');
}

function initPickerMap() {
  const { lat, lng } = currentSelectedCoordinate;
  if (!pickerMap) {
    pickerMap = L.map('picker-map').setView([lat, lng], 14);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '© OpenStreetMap'
    }).addTo(pickerMap);

    pickerMarker = L.marker([lat, lng], { draggable: true }).addTo(pickerMap);

    // On user tap on map
    pickerMap.on('click', e => {
      updatePickerLocation(e.latlng.lat, e.latlng.lng);
    });

    pickerMarker.on('dragend', () => {
      const pos = pickerMarker.getLatLng();
      updatePickerLocation(pos.lat, pos.lng);
    });
  } else {
    pickerMap.invalidateSize();
    pickerMap.setView([lat, lng], 14);
    pickerMarker.setLatLng([lat, lng]);
  }

  updatePickerLocation(lat, lng);
}

function updatePickerLocation(lat, lng) {
  currentSelectedCoordinate = { lat, lng };
  pickerMarker.setLatLng([lat, lng]);
  
  const nearest = findNearestTNDistrict(lat, lng);
  currentSelectedDistrict = nearest;

  document.getElementById('picker-district-text').innerText = `${nearest.name}, Tamil Nadu`;
  document.getElementById('picker-coords-text').innerText = `${lat.toFixed(5)}, ${lng.toFixed(5)}`;
  const ward = nearest.wards[0] || "Civic Ward";
  currentSelectedAddress = `${ward}, ${nearest.name}, Tamil Nadu`;
  document.getElementById('picker-addr-text').innerText = currentSelectedAddress;
}

function jumpMapPicker(name, lat, lng) {
  updatePickerLocation(lat, lng);
  pickerMap.setView([lat, lng], 14);
}

function confirmMapLocation() {
  applyLocationCoordinates(currentSelectedCoordinate.lat, currentSelectedCoordinate.lng, false);
  closeMapPickerModal();
}

// 38 Districts Modal
function openDistrictsModal() {
  document.getElementById('modal-districts').classList.remove('hidden');
}

function closeDistrictsModal() {
  document.getElementById('modal-districts').classList.add('hidden');
}

function filterRegion(region) {
  selectedRegionFilter = region;
  document.querySelectorAll('.region-pill').forEach(btn => {
    if (btn.innerText.includes(region) || (region === 'All' && btn.innerText.includes('All'))) {
      btn.className = 'region-pill active px-2.5 py-1 rounded-full bg-blue-600 text-white whitespace-nowrap font-medium text-[11px]';
    } else {
      btn.className = 'region-pill px-2.5 py-1 rounded-full bg-slate-100 text-slate-700 whitespace-nowrap font-medium text-[11px]';
    }
  });
  renderDistrictsList();
}

function renderDistrictsList() {
  const container = document.getElementById('districts-list-container');
  const query = (document.getElementById('district-search-input')?.value || '').toLowerCase();

  const filtered = ALL_38_DISTRICTS.filter(d => {
    const matchesRegion = selectedRegionFilter === 'All' || d.region === selectedRegionFilter;
    const matchesSearch = !query || 
      d.name.toLowerCase().includes(query) || 
      d.zone.toLowerCase().includes(query) || 
      d.wards.some(w => w.toLowerCase().includes(query));
    return matchesRegion && matchesSearch;
  });

  container.innerHTML = filtered.map(d => `
    <div class="bg-slate-50 border border-slate-200 rounded-2xl p-3 space-y-2">
      <div class="flex items-center justify-between">
        <div>
          <h4 class="font-bold text-sm text-slate-800">${d.name}</h4>
          <p class="text-[11px] text-slate-500">${d.zone}</p>
        </div>
        <span class="text-[10px] font-bold bg-blue-100 text-blue-800 px-2 py-0.5 rounded-full">${d.region}</span>
      </div>

      <!-- Ward Chips -->
      <div class="flex flex-wrap gap-1.5 pt-1">
        ${d.wards.map(w => `
          <button type="button" onclick="selectDistrictWard('${d.name}', '${w}', ${d.lat}, ${d.lng})" class="text-[11px] bg-white border border-slate-300 hover:border-blue-500 text-slate-700 px-2 py-0.5 rounded-md active:bg-blue-50">
            ${w}
          </button>
        `).join('')}
      </div>

      <button type="button" onclick="selectDistrictWard('${d.name}', '${d.wards[0]}', ${d.lat}, ${d.lng})" class="text-xs text-blue-600 font-bold flex items-center space-x-1 pt-1">
        <span>📍</span>
        <span>Select ${d.name} Center</span>
      </button>
    </div>
  `).join('');
}

function selectDistrictWard(districtName, ward, lat, lng) {
  currentSelectedCoordinate = { lat, lng };
  currentSelectedAddress = `${ward}, ${districtName}, Tamil Nadu`;
  
  document.getElementById('input-location').value = currentSelectedAddress;
  applyLocationCoordinates(lat, lng, false);
  closeDistrictsModal();
}

// Form Submission
function submitGrievance() {
  const title = document.getElementById('input-title').value.trim();
  const desc = document.getElementById('input-desc').value.trim();
  const dept = document.getElementById('input-dept').value;
  const loc = document.getElementById('input-location').value.trim() || currentSelectedAddress;
  const isAnon = document.getElementById('input-anon').checked;

  if (!title) {
    alert("Please enter an issue title.");
    return;
  }

  const newGrievance = {
    id: `CMP-TN-2026-${Math.floor(1000 + Math.random() * 9000)}`,
    title: title,
    description: desc || "Reported civic issue requiring municipal attention.",
    department: dept,
    location: loc,
    priority: currentPriority,
    status: "Submitted",
    lat: currentSelectedCoordinate.lat,
    lng: currentSelectedCoordinate.lng,
    upvotes: 1,
    isAnonymous: isAnon,
    createdAt: "Just now"
  };

  complaints.unshift(newGrievance);
  localStorage.setItem('tn_complaints', JSON.stringify(complaints));

  // Reset Form
  document.getElementById('input-title').value = '';
  document.getElementById('input-desc').value = '';
  document.getElementById('input-location').value = '';
  document.getElementById('gps-banner').classList.add('hidden');

  alert(`Grievance ${newGrievance.id} successfully registered with Tamil Nadu Civic Administration!`);
  switchTab('complaints');
  renderComplaintsList();
}

// Main Interactive Map
function initMainMap() {
  if (!mainMap) {
    mainMap = L.map('main-map').setView([11.1271, 78.6569], 7); // Central TN
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
      attribution: '© OpenStreetMap'
    }).addTo(mainMap);
  } else {
    mainMap.invalidateSize();
  }

  // Clear existing markers
  mainMap.eachLayer(layer => {
    if (layer instanceof L.Marker) {
      mainMap.removeLayer(layer);
    }
  });

  // Plot complaints
  complaints.forEach(c => {
    if (c.lat && c.lng) {
      const marker = L.marker([c.lat, c.lng]).addTo(mainMap);
      marker.bindPopup(`
        <div style="font-size: 12px; min-width: 160px;">
          <b style="color: #1976D2;">${c.title}</b><br/>
          <span>${c.location}</span><br/>
          <span style="font-size: 10px; color: #666;">Status: <b>${c.status}</b> | 👍 ${c.upvotes}</span>
        </div>
      `);
    }
  });
}

function centerUserGPSOnMainMap() {
  if (!navigator.geolocation) {
    alert("GPS not available.");
    return;
  }

  navigator.geolocation.getCurrentPosition(pos => {
    const lat = pos.coords.latitude;
    const lng = pos.coords.longitude;
    if (mainMap) {
      mainMap.setView([lat, lng], 13);
      L.circleMarker([lat, lng], { radius: 8, color: '#10B981', fillOpacity: 0.8 })
        .addTo(mainMap)
        .bindPopup("<b>Your Live Location</b>")
        .openPopup();
    }
  });
}

import { Marker, Popup } from 'react-leaflet';
import { divIcon } from 'leaflet';
import type { Location } from '../types/Location';
import { getMarkerColor } from '../utils/Aqi';

interface AqiMarkerProps {
  location: Location;
}

export function AqiMarker({ location }: AqiMarkerProps) {
  const color = getMarkerColor(location.aqiCategory);
  const lastMeasurement = location.measurements[0]; 

  const icon = divIcon({
    className: '',
    html: `<div style="background-color: ${color}; width: 16px; height: 16px; border-radius: 50%; border: 2px solid white;"></div>`,
  });

  return (
    <Marker position={[location.latitude, location.longitude]} icon={icon}>
      <Popup>
        <strong>{location.name}</strong>
        <br />
        PM2.5: {lastMeasurement?.value ?? '—'} µg/m³
        <br />
        Kategoria: {location.aqiCategory}
      </Popup>
    </Marker>
  );
}
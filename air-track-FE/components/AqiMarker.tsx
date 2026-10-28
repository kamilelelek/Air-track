import { Marker, Tooltip } from 'react-leaflet';
import { divIcon } from 'leaflet';
import type { Location } from '../types/Location';
import { getMarkerColor } from '../utils/Aqi';

interface AqiMarkerProps {
  location: Location;
  selected: boolean;
  onSelect: (id: number) => void;
}

export function AqiMarker({ location, selected, onSelect }: AqiMarkerProps) {
  const color = getMarkerColor(location.aqiCategory);
  const size = selected ? 22 : 14;

  const icon = divIcon({
    className: '',
    iconSize: [size, size],
    html: `<div class="aqi-dot${selected ? ' aqi-dot--selected' : ''}" style="background-color: ${color};"></div>`,
  });

  return (
    <Marker
      position={[location.latitude, location.longitude]}
      icon={icon}
      zIndexOffset={selected ? 1000 : 0}
      eventHandlers={{ click: () => onSelect(location.id) }}
    >
      <Tooltip direction="top" offset={[0, -size / 2]}>
        {location.name}
      </Tooltip>
    </Marker>
  );
}

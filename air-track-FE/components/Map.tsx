import { MapContainer, TileLayer } from "react-leaflet";
import 'leaflet/dist/leaflet.css';

    export function Map() {
  return (
    <MapContainer
      center={[52.0, 19.0]}
      zoom={6}
      style={{ height: '600px', width: '100%' }} 
    >
      <TileLayer
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
      />
    </MapContainer>
  );
}
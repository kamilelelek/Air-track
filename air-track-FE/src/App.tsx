import { useState } from 'react';
import { Map } from '../components/Map';
import { Sidebar } from '../components/Sidebar';
import { useLocations } from '../hooks/useLocations';
import './App.css';

function App() {
  const { locations, loading, error } = useLocations();
  const [selectedId, setSelectedId] = useState<number | null>(null);
  const selected = locations.find((l) => l.id === selectedId);

  return (
    <div className="app">
      <main className="map-area">
        <h1 className="app-title">Air Track</h1>
        <Map locations={locations} selectedId={selectedId} onSelect={setSelectedId} />
      </main>
      <Sidebar
        locations={locations}
        selected={selected}
        loading={loading}
        error={error}
        onSelect={setSelectedId}
        onBack={() => setSelectedId(null)}
      />
    </div>
  );
}

export default App;

import type { Location } from '../types/Location';
import { useLocationMeasurements } from '../hooks/useLocationMeasurements';
import { formatMeasuredAt, getAqiLabel, getMarkerColor, getParameterLabel } from '../utils/Aqi';

interface SidebarProps {
  locations: Location[];
  selected: Location | undefined;
  loading: boolean;
  error: string | null;
  onSelect: (id: number) => void;
  onBack: () => void;
}

export function Sidebar({ locations, selected, loading, error, onSelect, onBack }: SidebarProps) {
  return (
    <aside className="sidebar">
      {selected ? (
        <LocationDetails key={selected.id} location={selected} onBack={onBack} />
      ) : (
        <LocationList locations={locations} loading={loading} error={error} onSelect={onSelect} />
      )}
    </aside>
  );
}

function AqiBadge({ location }: { location: Location }) {
  return (
    <span className="aqi-badge">
      <span className="aqi-badge__dot" style={{ backgroundColor: getMarkerColor(location.aqiCategory) }} />
      {getAqiLabel(location.aqiCategory)}
    </span>
  );
}

interface LocationListProps {
  locations: Location[];
  loading: boolean;
  error: string | null;
  onSelect: (id: number) => void;
}

function LocationList({ locations, loading, error, onSelect }: LocationListProps) {
  const sorted = [...locations].sort((a, b) => a.name.localeCompare(b.name, 'pl'));

  return (
    <>
      <header className="sidebar__header">
        <h2>Czujniki</h2>
        <p className="sidebar__subtitle">{loading ? 'Ładowanie…' : `${locations.length} stacji pomiarowych`}</p>
      </header>
      {error && <p className="sidebar__message">Nie udało się pobrać lokalizacji.</p>}
      <ul className="location-list">
        {sorted.map((location) => (
          <li key={location.id}>
            <button className="location-list__item" onClick={() => onSelect(location.id)}>
              <span className="location-list__dot" style={{ backgroundColor: getMarkerColor(location.aqiCategory) }} />
              <span className="location-list__text">
                <span className="location-list__name">{location.name}</span>
                <span className="location-list__city">{location.city}</span>
              </span>
              {location.pm25 != null && (
                <span className="location-list__value">
                  {location.pm25.toFixed(0)}
                  <small> PM2.5</small>
                </span>
              )}
            </button>
          </li>
        ))}
      </ul>
    </>
  );
}

function LocationDetails({ location, onBack }: { location: Location; onBack: () => void }) {
  const { measurements, hasMore, loading, error, loadMore } = useLocationMeasurements(location.id);

  return (
    <>
      <header className="sidebar__header">
        <button className="back-button" onClick={onBack}>← Wszystkie czujniki</button>
        <h2>{location.name}</h2>
        <p className="sidebar__subtitle">
          {location.city}, {location.country}
        </p>
        <AqiBadge location={location} />
      </header>

      <div className="sidebar__body">
        <section className="details-section">
          <h3>Ostatnie pomiary</h3>
          {location.measurements.length === 0 ? (
            <p className="sidebar__message">Brak aktualnych pomiarów.</p>
          ) : (
            <div className="latest-grid">
              {location.measurements.map((m) => (
                <div key={m.parameter} className="latest-card">
                  <span className="latest-card__label">{getParameterLabel(m.parameter)}</span>
                  <span className="latest-card__value">{m.value}</span>
                  <span className="latest-card__unit">{m.unit}</span>
                </div>
              ))}
            </div>
          )}
          {location.measurements[0] && (
            <p className="details-section__note">Stan na {formatMeasuredAt(location.measurements[0].measuredAt)}</p>
          )}
        </section>

        <section className="details-section">
          <h3>Lokalizacja</h3>
          <dl className="info-list">
            <dt>ID stacji</dt>
            <dd>{location.id}</dd>
            <dt>Szerokość</dt>
            <dd>{location.latitude.toFixed(5)}</dd>
            <dt>Długość</dt>
            <dd>{location.longitude.toFixed(5)}</dd>
          </dl>
        </section>

        <section className="details-section">
          <h3>Historia pomiarów</h3>
          {error && <p className="sidebar__message">Nie udało się pobrać historii pomiarów.</p>}
          {measurements.length > 0 && (
            <table className="history-table">
              <thead>
                <tr>
                  <th>Czas</th>
                  <th>Parametr</th>
                  <th>Wartość</th>
                </tr>
              </thead>
              <tbody>
                {measurements.map((m, i) => (
                  <tr key={`${m.measuredAt}-${m.parameter}-${i}`}>
                    <td>{formatMeasuredAt(m.measuredAt)}</td>
                    <td>{getParameterLabel(m.parameter)}</td>
                    <td>
                      {m.value} <small>{m.unit}</small>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
          {loading && <p className="sidebar__message">Ładowanie…</p>}
          {!loading && hasMore && (
            <button className="load-more" onClick={loadMore}>Pokaż starsze</button>
          )}
        </section>
      </div>
    </>
  );
}

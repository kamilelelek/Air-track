import type { AqiCategory } from '../types/AqiCategory';

export function getMarkerColor(category: AqiCategory | null): string {
  switch (category) {
    case 'DOBRY':
      return '#2fb67c';
    case 'UMIARKOWANY':
      return '#f2b233';
    case 'ZLY':
      return '#e5484d';
    default:
      return '#a5a8ad';
  }
}

export function getAqiLabel(category: AqiCategory | null): string {
  switch (category) {
    case 'DOBRY':
      return 'Dobra';
    case 'UMIARKOWANY':
      return 'Umiarkowana';
    case 'ZLY':
      return 'Zła';
    default:
      return 'Brak danych';
  }
}

const PARAMETER_LABELS: Record<string, string> = {
  pm25: 'PM2.5',
  pm10: 'PM10',
  no2: 'NO₂',
  o3: 'O₃',
  so2: 'SO₂',
  co: 'CO',
};

export function getParameterLabel(parameter: string): string {
  return PARAMETER_LABELS[parameter] ?? parameter.toUpperCase();
}

const dateFormat = new Intl.DateTimeFormat('pl-PL', {
  timeZone: 'Europe/Warsaw',
  day: '2-digit',
  month: '2-digit',
  hour: '2-digit',
  minute: '2-digit',
});

export function formatMeasuredAt(iso: string): string {
  return dateFormat.format(new Date(iso));
}

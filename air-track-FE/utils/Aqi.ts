import type { AqiCategory } from '../types/AqiCategory';

export function getMarkerColor(category: AqiCategory): 'green' | 'yellow' | 'red' {
  switch (category) {
    case 'DOBRY':
      return 'green';
    case 'UMIARKOWANY':
      return 'yellow';
    case 'ZLY':
      return 'red';
  }
}
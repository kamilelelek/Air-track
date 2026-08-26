import { useState, useEffect } from 'react';
import { Measurement } from '../types/Measurement';
import { getAllMeasurementsOfLocation } from '../api/Locations';
import { PaginatedResponse } from '../types/PaginatedResponse';

interface UseLocationMeasurementsResult {
  locationMeasurements: PaginatedResponse<Measurement> | null;
  loading: boolean;
  error: string | null;
}

export function useLocationMeasurements(locationId: string): UseLocationMeasurementsResult {
  const [locationMeasurements, setLocationMeasurements] = useState<PaginatedResponse<Measurement> | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    async function fetchData() {
      try {
        setLoading(true);
        const data = await getAllMeasurementsOfLocation(parseInt(locationId), 1); // Assuming page 1 for initial fetch
        setLocationMeasurements(data);
        setError(null);
      } catch (error) {
        setError('Failed to get all location measurements');
      } finally {
        setLoading(false);
      }
    }
    fetchData();
  }, [locationId]);

  return { locationMeasurements, loading, error };
}
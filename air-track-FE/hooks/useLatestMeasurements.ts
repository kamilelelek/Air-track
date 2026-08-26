import { useState, useEffect } from 'react';
import { getLatestMeasurements } from "../api/Locations";
import { Measurement } from "../types/Measurement";

interface UseLatestMeasurementsResult {
  latestMeasurements: Measurement[] | null;
  loading: boolean;
  error: string | null;
}

export function useLatestMeasurements(): UseLatestMeasurementsResult {
  const [latestMeasurements, setLatestMeasurements] = useState<Measurement[] | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    async function fetchData() {
      try {
        setLoading(true);
        const data = await getLatestMeasurements();
        setLatestMeasurements(data);
        setError(null);
      } catch (error) {
        setError('Failed to get latest measurements');
      } finally {
        setLoading(false);
      }
    }
    fetchData();
  }, []);

  return { latestMeasurements, loading, error };
}
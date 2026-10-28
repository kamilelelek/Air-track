import { useState, useEffect } from 'react';
import type { Measurement } from '../types/Measurement';
import { getAllMeasurementsOfLocation } from '../api/Locations';

interface UseLocationMeasurementsResult {
  measurements: Measurement[];
  hasMore: boolean;
  loading: boolean;
  error: string | null;
  loadMore: () => void;
}

export function useLocationMeasurements(locationId: number): UseLocationMeasurementsResult {
  const [measurements, setMeasurements] = useState<Measurement[]>([]);
  const [page, setPage] = useState<number>(0);
  const [totalPages, setTotalPages] = useState<number>(0);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  // Pages accumulate for one location; remount the caller (key) to switch locations.
  useEffect(() => {
    let cancelled = false;
    async function fetchData() {
      try {
        setLoading(true);
        const data = await getAllMeasurementsOfLocation(locationId, page);
        if (cancelled) return;
        setMeasurements((prev) => (page === 0 ? data.content : [...prev, ...data.content]));
        setTotalPages(data.totalPages);
        setError(null);
      } catch {
        if (!cancelled) setError('Failed to get all location measurements');
      } finally {
        if (!cancelled) setLoading(false);
      }
    }
    fetchData();
    return () => {
      cancelled = true;
    };
  }, [locationId, page]);

  return {
    measurements,
    hasMore: page + 1 < totalPages,
    loading,
    error,
    loadMore: () => setPage((p) => p + 1),
  };
}

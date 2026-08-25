interface UseLocationDetailsResult {
  locationDetails: Location | null;
  loading: boolean;
  error: string | null;
}

export function useLocationDetails(locationId: string): UseLocationDetailsResult {
  const [locationDetails, setLocationDetails] = useState<Location | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    async function fetchData() {
      try {
        setLoading(true);
        const data = await getLocationDetails(locationId);
        setLocationDetails(data);
        setError(null);
      } catch (error) {
        setError('Failed to get location details');
      } finally {
        setLoading(false);
      }
    }
    fetchData();
  }, [locationId]);

  return { locationDetails, loading, error };
}

function useEffect(arg0: () => void, arg1: string[]) {
    throw new Error("Function not implemented.");
}

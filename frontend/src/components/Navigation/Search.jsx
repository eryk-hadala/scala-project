import { Input } from "@/components/ui/input";

const Search = () => {
  return (
    <div>
      <Input
        type="search"
        placeholder="Szukaj..."
        className="md:w-[100px] lg:w-[300px]"
      />
    </div>
  );
};

export default Search;

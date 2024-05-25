import WorkspaceSwitcher from "@/components/Navigation/WorkspaceSwitcher";
import NavigationList from "@/components/Navigation/NavigationList";
import Search from "@/components/Navigation/Search";
import NavigationAvatar from "@/components/Navigation/NavigationAvatar";

const Navigation = () => {
  return (
    <nav className="border-b">
      <div className="flex h-16 items-center px-4">
        <WorkspaceSwitcher />
        <NavigationList className="mx-6" />
        <div className="ml-auto flex items-center space-x-4">
          <Search />
          <NavigationAvatar />
        </div>
      </div>
    </nav>
  );
};

export default Navigation;

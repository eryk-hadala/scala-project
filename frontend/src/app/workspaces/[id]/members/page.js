"use client";

import { useMembers } from "../../_api/getMembers";
import MembersTable from "../../_components/MembersTable/MembersTable";

export default function MembersPage() {
  const { members, isFetching } = useMembers();

  return (
    <div className="p-8">
      <MembersTable data={members} isLoading={isFetching} />
      {/* {members.map(({ id, username }) => (
        <div key={id}>{username}</div>
      ))} */}
    </div>
  );
}

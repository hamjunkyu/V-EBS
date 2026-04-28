type FormRowProps = {
  label: string;
  children: React.ReactNode;
  last?: boolean;
};

export default function FormRow({ label, children, last = false }: FormRowProps) {
  return (
    <div className={`flex ${last ? "" : "border-b border-gray-300"}`}>
      <div className="w-44 shrink-0 bg-gray-100 px-4 py-5 font-medium text-center flex items-center justify-center border-r border-gray-300 text-gray-700">
        {label}
      </div>
      <div className="flex-1 px-6 py-5">{children}</div>
    </div>
  );
}

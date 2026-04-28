"use client";

import { useState } from "react";
import FormRow from "@/components/FormRow";

const TIME_SLOTS = [
  "12:00", "12:25", "12:50", "13:15", "13:40", "14:05", "14:30", "15:00",
  "15:25", "15:50", "16:15", "16:40", "17:05", "17:30", "18:00", "18:25",
  "18:50", "19:20", "19:45", "20:10", "20:35", "21:00", "21:25", "21:50",
  "22:15", "22:40",
];

const UNAVAILABLE_SLOTS = ["12:00", "13:40", "17:05", "22:15", "22:40"];

const COURSES = [
  { value: "smart-phonics", label: "스마트파닉스" },
  { value: "travel-english", label: "여행영어" },
  { value: "free-talking", label: "프리토킹" },
];

function getDateString(daysFromNow: number) {
  const date = new Date();
  date.setDate(date.getDate() + daysFromNow);
  return date.toISOString().split("T")[0];
}

function formatShortDate(iso: string) {
  const [, m, d] = iso.split("-");
  return `${Number(m)}/${Number(d)}`;
}

export default function EnrollmentPage() {
  const [course, setCourse] = useState("travel-english");
  const [daySchedule, setDaySchedule] = useState("mon-wed-fri");
  const [startDate, setStartDate] = useState("");
  const [selectedTime, setSelectedTime] = useState<string | null>(null);

  const minDate = getDateString(1);
  const maxDate = getDateString(7);

  const canSubmit = startDate !== "" && selectedTime !== null;

  const handleSubmit = () => {
    const courseLabel = COURSES.find((c) => c.value === course)?.label;
    const dayLabel = daySchedule === "mon-wed-fri" ? "월 수 금" : "화 목";
    alert(
      `수강신청이 완료되었습니다.\n\n과정: ${courseLabel}\n수업요일: ${dayLabel}\n시작일: ${startDate}\n시간: ${selectedTime}`
    );
  };

  const handleCancel = () => {
    setCourse("travel-english");
    setDaySchedule("mon-wed-fri");
    setStartDate("");
    setSelectedTime(null);
  };

  const inputClass = "min-w-[220px] px-3 py-2 border border-gray-400 rounded bg-white text-sm";

  return (
    <main className="max-w-5xl mx-auto px-6 py-10 bg-white text-gray-900">
      <h1 className="text-2xl font-bold mb-3">화상영어 수강신청</h1>
      <hr className="border-gray-300 mb-4" />
      <p className="text-sky-500 text-sm mb-8">※ 첫 수업은 레벨테스트로 진행됩니다.</p>

      <div className="border border-gray-300">
        <FormRow label="과정">
          <div className="flex items-center gap-2">
            <select
              value={course}
              onChange={(e) => setCourse(e.target.value)}
              className={inputClass}
            >
              {COURSES.map((c) => (
                <option key={c.value} value={c.value}>
                  {c.label}
                </option>
              ))}
            </select>
            <span className="text-sm text-gray-700 ml-1">&lt;과정보기&gt;</span>
          </div>
          <p className="text-sky-500 text-xs mt-2">
            ※ 수업과정은 강사님의 추천과정에 의해 변경 될 수 있습니다.
          </p>
        </FormRow>

        <FormRow label="수업요일">
          <select
            value={daySchedule}
            onChange={(e) => setDaySchedule(e.target.value)}
            className={inputClass}
          >
            <option value="mon-wed-fri">월 수 금</option>
            <option value="tue-thu">화 목</option>
          </select>
        </FormRow>

        <FormRow label="수업 시작일">
          <input
            type="date"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
            min={minDate}
            max={maxDate}
            className={inputClass}
          />
          <p className="text-xs text-gray-500 mt-2">
            내일부터 7일 후까지 선택 가능합니다. ({formatShortDate(minDate)} ~ {formatShortDate(maxDate)})
          </p>
        </FormRow>

        <FormRow label="수업시간 선택" last>
          <div className="inline-grid grid-cols-8 border-t border-l border-gray-300">
            {TIME_SLOTS.map((time) => {
              const isUnavailable = UNAVAILABLE_SLOTS.includes(time);
              const isSelected = selectedTime === time;
              return (
                <button
                  key={time}
                  type="button"
                  disabled={isUnavailable}
                  onClick={() => setSelectedTime(time)}
                  className={`w-[90px] py-3 text-sm text-center border-r border-b border-gray-300 ${
                    isUnavailable
                      ? "text-gray-500 line-through cursor-not-allowed"
                      : isSelected
                        ? "bg-amber-500 text-white"
                        : "text-amber-500 hover:bg-amber-50 cursor-pointer"
                  }`}
                >
                  {time}
                </button>
              );
            })}
          </div>
        </FormRow>
      </div>

      <div className="flex justify-center gap-6 mt-10">
        <button
          type="button"
          onClick={handleSubmit}
          disabled={!canSubmit}
          className="bg-sky-500 text-white px-16 py-3 rounded font-semibold hover:bg-sky-600 cursor-pointer disabled:bg-gray-300 disabled:cursor-not-allowed disabled:hover:bg-gray-300"
        >
          신 청
        </button>
        <button
          type="button"
          onClick={handleCancel}
          className="bg-gray-400 text-white px-16 py-3 rounded font-semibold hover:bg-gray-500 cursor-pointer"
        >
          취 소
        </button>
      </div>
    </main>
  );
}

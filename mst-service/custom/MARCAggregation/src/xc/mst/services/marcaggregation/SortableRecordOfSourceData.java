package xc.mst.services.marcaggregation;

import java.util.Date;
import java.util.List;

import xc.mst.repo.Repository;


    /**
     * assumptions - invalid class state of leader_byte17_wt_en == false && bigger_record_wt_en == false caught before now.
     * BUT, we'll still throw an exception if it is encountered here.
     *
     # Record of source criteria:
     #
     # 1) leader_byte17_weighting_enabled = true/false
     # 2) bigger_record_weighting_enabled = true/false
     #
     # And four cases:
     #
     # 1-true, 2-false
     # In this case we first compare Leader/byte17, pick the earliest (in String leader.order above),
     #   if they are the same, pick the record that is being processed.
     #
     # 1-true, 2-true
     # In this case we first compare Leader/byte17, pick the earliest (in String leader.order above),
     #   if they are the same, pick the record that is largest in bytes.
     #
     # 1-false, 2-true
     # Pick the record that is largest in bytes.
     #
     # 1-false, 2-false
     # This is a not-allowed state and the service will throw an error message.
     #
     * @author John Brand
     *
     */
    public class SortableRecordOfSourceData implements Comparable<SortableRecordOfSourceData> {
        Repository repo;
        List<Character> leaderVals;
        long recordId;
        RecordOfSourceData source;
        boolean leader_byte17_wt_en;
        boolean bigger_record_wt_en;

        public SortableRecordOfSourceData(Repository repo, List<Character> leaderVals, long recordId, RecordOfSourceData source,
                boolean leader_byte17_wt_en, boolean bigger_record_wt_en) {
            this.repo = repo;
            this.leaderVals = leaderVals;
            this.recordId = recordId;
            this.source = source;
            this.leader_byte17_wt_en = leader_byte17_wt_en;
            this.bigger_record_wt_en = bigger_record_wt_en;
        }

        public boolean equals(Object that) {
            if (that == null) return false;
            if ( this == that ) return true;
            if ( !(that instanceof SortableRecordOfSourceData) ) return false;

            SortableRecordOfSourceData s = (SortableRecordOfSourceData) that;
            if (this.recordId == s.recordId && this.source.leaderByte17 == s.source.leaderByte17 && this.source.size == s.source.size) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return new Long(recordId).hashCode();
        }

        @Override
        public int compareTo(SortableRecordOfSourceData o2) {
            SortableRecordOfSourceData o1 = this;
            if (o1.equals(o2)) return 0;

            if (leader_byte17_wt_en) {
                //now, 1st in the list is the one with the leader earliest in the leaderVals list.
                Character o1Leader = o1.source.leaderByte17;
                Character o2Leader = o2.source.leaderByte17;
                int leader1pos;
                int leader2pos;
                if (leaderVals.contains(o1Leader)) {
                    leader1pos = leaderVals.indexOf(o1Leader);
                    if (leaderVals.contains(o2Leader)) {
                        leader2pos = leaderVals.indexOf(o2Leader);
                        if (leader1pos == leader2pos) {
                            //must continue;
                        }
                        else if (leader1pos < leader2pos) {
                            return -1;
                        }
                        else {
                            return 1;
                        }
                    }
                    else {
                        // have a leader1 value but not a leader 2 value, so leader1 is higher on the food chain
                        return -1;
                    }
                }
                else if (leaderVals.contains(o2Leader)) {
                    // have a leader2 value but not a leader 1 value, so leader2 is higher on the food chain
                    return 1;
                }
            }
            else if (bigger_record_wt_en) {
                if (o1.source.size != o2.source.size) {
                    return compareRecordOfSourceSize(o1.source.size, o2.source.size);
                }
                else {
                    // we are going to the tie-breaker
                    return compareRecordOfSourceDateUpdated(repo.getRecord(o1.recordId).getUpdatedAt(), repo.getRecord(o2.recordId).getUpdatedAt());
                }
            }
            else {
                throw new RuntimeException("record of source invalid state, both bigger & leader comparisons disabled");
            }

            // at this point, the leader vals are functionally equivalent so go to the next criteria!
            if (bigger_record_wt_en) {
                if (o1.source.size != o2.source.size) {
                    return compareRecordOfSourceSize(o1.source.size, o2.source.size);
                }
                else {
                    // we are going to the tie-breaker
                    return compareRecordOfSourceDateUpdated(repo.getRecord(o1.recordId).getUpdatedAt(), repo.getRecord(o2.recordId).getUpdatedAt());
                }
            }
            else {
                // we are going to the tie-breaker
                return compareRecordOfSourceDateUpdated(repo.getRecord(o1.recordId).getUpdatedAt(), repo.getRecord(o2.recordId).getUpdatedAt());
            }
        }

        // since this is the tie-breaker, make sure to return a 1 or -1
        protected int compareRecordOfSourceDateUpdated(Date o1, Date o2) {
            if (o1.after(o2)) {
                //prefer o1
                return -1;
            }
            else {
                //prefer o2
                return 1;
            }
        }

        protected int compareRecordOfSourceSize(int o1, int o2) {
            if (o1 > o2) {
                return -1;
            }
            else if (o1 < o2) {
                return 1;
            }
            else return 0;
        }
    }

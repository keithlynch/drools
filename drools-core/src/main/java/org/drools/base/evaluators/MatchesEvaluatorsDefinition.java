/*
 * Copyright 2007 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Created on Dec 6, 2007
 */
package org.drools.base.evaluators;

import org.drools.base.BaseEvaluator;
import org.drools.base.ValueType;
import org.drools.common.InternalWorkingMemory;
import org.drools.rule.VariableRestriction.ObjectVariableContextEntry;
import org.drools.rule.VariableRestriction.VariableContextEntry;
import org.drools.spi.Evaluator;
import org.drools.spi.InternalReadAccessor;
import org.drools.spi.FieldValue;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import java.util.Map;

/**
 * This class defines the matches evaluator
 *
 * @author etirelli
 */
public class MatchesEvaluatorsDefinition implements EvaluatorDefinition {

    public static final Operator  MATCHES       = Operator.addOperatorToRegistry( "matches",
                                                                                  false );
    public static final Operator  NOT_MATCHES   = Operator.addOperatorToRegistry( "matches",
                                                                                  true );

    private static final String[] SUPPORTED_IDS = { MATCHES.getOperatorString() };
    private EvaluatorCache evaluators = new EvaluatorCache() {
        private static final long serialVersionUID = 4782368623L;
        {
            addEvaluator( ValueType.STRING_TYPE,        MATCHES,         StringMatchesEvaluator.INSTANCE );
            addEvaluator( ValueType.STRING_TYPE,        NOT_MATCHES,     StringNotMatchesEvaluator.INSTANCE );
        }
    };

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        evaluators  = (EvaluatorCache)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(evaluators);
    }

    /**
     * @inheridDoc
     */
    public Evaluator getEvaluator(ValueType type,
                                  Operator operator) {
        return this.evaluators.getEvaluator( type,
                                             operator );
    }

    /**
     * @inheridDoc
     */
    public Evaluator getEvaluator(ValueType type,
                                  Operator operator,
                                  String parameterText) {
        return this.evaluators.getEvaluator( type,
                                             operator );
    }


    public Evaluator getEvaluator(final ValueType type,
                                  final String operatorId,
                                  final boolean isNegated,
                                  final String parameterText) {
        return this.evaluators.getEvaluator( type, Operator.determineOperator( operatorId, isNegated ) );
    }

    public String[] getEvaluatorIds() {
        return SUPPORTED_IDS;
    }

    public boolean isNegatable() {
        return true;
    }

    public boolean operatesOnFactHandles() {
        return false;
    }

    public boolean supportsType(ValueType type) {
        return this.evaluators.supportsType( type );
    }

    /*  *********************************************************
     *           Evaluator Implementations
     *  *********************************************************
     */
    public static class StringMatchesEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new StringMatchesEvaluator();


        public StringMatchesEvaluator() {
            super( ValueType.STRING_TYPE,
                   MATCHES );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            final String value1 = (String) extractor.getValue( workingMemory, object1 );
            final String value2 = (String) object2.getValue();
            if ( value1 == null ) {
                return false;
            }
            return value1.matches( value2 );
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            final String value = (String) ((ObjectVariableContextEntry) context).right;
            if ( value == null ) {
                return false;
            }
            return value.matches( (String) context.declaration.getExtractor().getValue( workingMemory, left ) );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            final String value = (String) context.extractor.getValue( workingMemory, right );
            if ( value == null ) {
                return false;
            }
            return value.matches( (String) ((ObjectVariableContextEntry) context).left );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            final Object value1 = extractor1.getValue( workingMemory, object1 );
            final Object value2 = extractor2.getValue( workingMemory, object2 );
            if ( value1 == null ) {
                return false;
            }
            return ((String) value1).matches( (String) value2 );
        }

        public String toString() {
            return "String matches";
        }
    }

    public static class StringNotMatchesEvaluator extends BaseEvaluator {
        /**
         *
         */
        private static final long     serialVersionUID = 400L;
        public final static Evaluator INSTANCE         = new StringNotMatchesEvaluator();

        public StringNotMatchesEvaluator() {
            super( ValueType.STRING_TYPE,
                   NOT_MATCHES );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor,
                                final Object object1, final FieldValue object2) {
            final String value1 = (String) extractor.getValue( workingMemory, object1 );
            final String value2 = (String) object2.getValue();
            if ( value1 == null ) {
                return false;
            }
            return ! value1.matches( value2 );
        }

        public boolean evaluateCachedRight(InternalWorkingMemory workingMemory,
                                           final VariableContextEntry context, final Object left) {
            final String value = (String) ((ObjectVariableContextEntry) context).right;
            if ( value == null ) {
                return false;
            }
            return ! value.matches( (String) context.declaration.getExtractor().getValue( workingMemory, left ) );
        }

        public boolean evaluateCachedLeft(InternalWorkingMemory workingMemory,
                                          final VariableContextEntry context, final Object right) {
            final String value = (String) context.extractor.getValue( workingMemory, right );
            if ( value == null ) {
                return false;
            }
            return ! value.matches( (String) ((ObjectVariableContextEntry) context).left );
        }

        public boolean evaluate(InternalWorkingMemory workingMemory,
                                final InternalReadAccessor extractor1,
                                final Object object1,
                                final InternalReadAccessor extractor2, final Object object2) {
            final Object value1 = extractor1.getValue( workingMemory, object1 );
            final Object value2 = extractor2.getValue( workingMemory, object2 );
            if ( value1 == null ) {
                return false;
            }
            return ! ((String) value1).matches( (String) value2 );
        }

        public String toString() {
            return "String not matches";
        }
    }

}
